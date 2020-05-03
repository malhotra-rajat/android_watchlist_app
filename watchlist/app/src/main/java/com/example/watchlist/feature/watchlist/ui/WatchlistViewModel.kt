package com.example.watchlist.feature.watchlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseStatus
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.watchlist.datamodels.Item
import com.example.watchlist.feature.watchlist.datamodels.Quote
import com.example.watchlist.feature.watchlist.db.Symbol
import com.example.watchlist.feature.watchlist.db.Watchlist
import com.example.watchlist.feature.watchlist.db.WatchlistDatabase
import com.example.watchlist.feature.watchlist.domain.HistoricalPriceItem
import com.example.watchlist.feature.watchlist.repositories.IEXRepository
import com.example.watchlist.feature.watchlist.repositories.SymbolRepository
import com.example.watchlist.feature.watchlist.repositories.TastyworksRepository
import com.example.watchlist.feature.watchlist.repositories.WatchlistRepository
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = this::class.java.simpleName

    //loading states variables
    val watchlistLoadingState = MutableLiveData<State>()
    val message = MutableLiveData<String>()

    //quotes variables
    private val quotesMap = LinkedHashMap<String, Quote>()
    val quotesMapLiveData = MutableLiveData<LinkedHashMap<String, Quote>>()

    //auto complete search variables
    private val searchResults = ArrayList<Item>()
    val searchResultsLiveData = MutableLiveData<ArrayList<Item>>()

    //chart variables
    val chartEntriesLiveData = MutableLiveData<ArrayList<Entry>>()
    var chartDates = ArrayList<String>()
    val chartLoadingState = MutableLiveData<State>()

    //db daos and repositories
    private val watchlistDao = WatchlistDatabase.getDatabase(application).watchlistDao()
    private val watchlistRepository = WatchlistRepository(watchlistDao)
    private val symbolDao = WatchlistDatabase.getDatabase(application).symbolDao()
    private val symbolRepository = SymbolRepository(symbolDao)

    //db LiveData objects
    var allWatchlists = watchlistRepository.getAll()
    var currentSymbolsLiveData = symbolRepository.getSymbolsForWatchlist(0)

    var currentSymbolsMap = mutableMapOf<String, Long>()

    private var fetchQuotesJob: Job? = null

    fun startQuoteFetchJob(symbolStrings: ArrayList<String>) {
        clearQuotes()
        watchlistLoadingState.postValue(State.Loading)
        stopFetchingQuotes()
        fetchQuotesJob = getQuotesJob(symbolStrings)
    }

    fun stopFetchingQuotes() {
        fetchQuotesJob?.cancel()
    }

    fun addInitialWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            val watchlist = Watchlist(
                0,
                watchlistName = "My first list"
            )
            addWatchlist(watchlist)

            val symbols = mutableListOf("AAPL", "MSFT", "GOOG")

            symbols.forEach {
                addSymbolToWatchList(
                    Symbol(
                        name = it,
                        watchlistId = watchlist.id
                    )
                )
            }
            message.postValue("Initial watchlist added")
            //selectedWatchlistId = watchlist.id
        }
    }

    fun addWatchlist(watchlist: Watchlist) = viewModelScope.launch(Dispatchers.IO) {
        watchlistRepository.insert(watchlist)
    }


    fun deleteWatchlist(watchlist: Watchlist) = viewModelScope.launch(Dispatchers.IO) {
        watchlistRepository.delete(watchlist)
    }


    fun removeSymbolFromWatchList(symbolId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            symbolRepository.deleteById(symbolId)
            //message.postValue("${symbol.name} removed")
        }
    }

    fun addSymbolToWatchList(symbol: Symbol) {
        viewModelScope.launch(Dispatchers.IO) {
            symbolRepository.insert(symbol)
            message.postValue("${symbol.name} added")
        }
    }


    private fun getQuotesJob(symbolStrings: ArrayList<String>): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val apiCalls = mutableListOf<Deferred<Unit>>()

                symbolStrings.forEach {
                    apiCalls.add(async { getQuote(it) })
                }
                //Wait for all calls to return
                apiCalls.awaitAll()
                watchlistLoadingState.postValue(State.Done)
                quotesMapLiveData.postValue(quotesMap)

                delay(5000)
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

    fun clearQuotes() {
        quotesMap.clear()
        quotesMapLiveData.postValue(quotesMap)
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
        message.postValue(resource.message)
    }

    override fun onCleared() {
        super.onCleared()
        stopFetchingQuotes()
    }
}