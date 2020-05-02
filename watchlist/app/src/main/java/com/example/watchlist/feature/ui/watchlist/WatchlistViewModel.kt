package com.example.watchlist.feature.ui.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseStatus
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.datamodels.api.Item
import com.example.watchlist.feature.datamodels.api.Quote
import com.example.watchlist.feature.datamodels.db.Watchlist
import com.example.watchlist.feature.db.WatchlistDatabase
import com.example.watchlist.feature.domain.HistoricalPriceItem
import com.example.watchlist.feature.repositories.IEXRepository
import com.example.watchlist.feature.repositories.TastyworksRepository
import com.example.watchlist.feature.repositories.WatchlistRepository
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = this::class.java.simpleName

    val watchlistLoadingState = MutableLiveData<State>()
    val error = MutableLiveData<String>()

    private val watchlist = ArrayList<String>()

    private val quotesMap = LinkedHashMap<String, Quote>()
    val quotesMapLiveData = MutableLiveData<LinkedHashMap<String, Quote>>()

    private val searchResults = ArrayList<Item>()
    val searchResultsLiveData = MutableLiveData<ArrayList<Item>>()

    val chartEntriesLiveData = MutableLiveData<ArrayList<Entry>>()
    var chartDates = ArrayList<String>()

    val chartLoadingState = MutableLiveData<State>()
    private val fetchQuotesJob: Job

    private val watchlistRepository: WatchlistRepository
    var watchlists: LiveData<List<Watchlist>>

    init {
        watchlistLoadingState.postValue(State.Loading)
        watchlist.add("AAPL")
        watchlist.add("MSFT")
        watchlist.add("GOOG")
        fetchQuotesJob = getQuotesJob()
        fetchQuotesJob.start()


        val wordsDao = WatchlistDatabase.getDatabase(application).watchlistDao()
        watchlistRepository = WatchlistRepository(wordsDao)

        watchlists = watchlistRepository.allWatchlists
    }

    fun addWatchlist(watchlist: Watchlist) = viewModelScope.launch(Dispatchers.IO) {
        watchlistRepository.insert(watchlist)
    }


    fun deleteWatchlist(watchlist: Watchlist) = viewModelScope.launch(Dispatchers.IO) {
        watchlistRepository.delete(watchlist)
    }


    fun removeSymbolFromWatchList(symbol: String) {
        watchlist.remove(symbol)
    }

    fun addSymbolToWatchList(symbol: String) {
        watchlist.add(symbol)
        watchlistLoadingState.postValue(State.Loading)
    }


    private fun getQuotesJob(): Job {
        return viewModelScope.launch {
            while (isActive) {
                val apiCalls = mutableListOf<Deferred<Unit>>()
                watchlist.forEach {
                    apiCalls.add(async { getQuote(it) })
                }
                //Wait for all calls to return
                apiCalls.awaitAll()
                watchlistLoadingState.postValue(State.Done)
                quotesMapLiveData.postValue(quotesMap)

                delay(100000)
            }
        }
    }

    private suspend fun getQuote(symbol: String) {
        val quoteResource = IEXRepository().getQuote(symbol)
        when (quoteResource.responseStatus) {
            ResponseStatus.SUCCESS -> {
                quoteResource.data?.let {
                    quotesMap.put(symbol, it)
                }
            }

            ResponseStatus.ERROR -> {
                handleFailure(quoteResource)
            }
        }
    }

    fun searchSymbol(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val symbolsResource = TastyworksRepository().searchSymbol(query)
            when (symbolsResource.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    searchResults.clear()

                    symbolsResource.data?.data?.items?.let {
                        searchResults.addAll(it)
                        searchResultsLiveData.postValue(searchResults)
                    }

                    watchlistLoadingState.postValue(State.Done)
                }

                ResponseStatus.ERROR -> {
                    handleFailure(symbolsResource)
                }
            }
        }
    }


    suspend fun getHistoricalPrices(symbol: String) {
        chartLoadingState.postValue(State.Loading)
        val historicalPricesResource = IEXRepository().getHistoricalPrices(symbol, "1m")
        when (historicalPricesResource.responseStatus) {
            ResponseStatus.SUCCESS -> {
                chartLoadingState.postValue(State.Done)
                historicalPricesResource.data?.let { historicalPriceItems ->
                    chartEntriesLiveData.postValue(getEntries(historicalPriceItems))
                }
            }

            ResponseStatus.ERROR -> {
                handleFailure(historicalPricesResource)
            }
        }
    }

    private fun getEntries(historicalPriceItems: ArrayList<HistoricalPriceItem>): ArrayList<Entry> {
        chartDates.clear()
        val entries = ArrayList<Entry>()
        for ((index, historicalPriceItem) in historicalPriceItems.withIndex()) {
            entries.add(Entry(index.toFloat(), historicalPriceItem.averagePrice.toFloat()))
            historicalPriceItem.date?.let { chartDates.add(it) }
        }
        return entries

    }

    private fun handleFailure(resource: Resource<Any>) {
        watchlistLoadingState.postValue(State.Error)
        chartLoadingState.postValue(State.Error)
        error.postValue(resource.message)
    }

    override fun onCleared() {
        super.onCleared()
        fetchQuotesJob.cancel()
    }
}