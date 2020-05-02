package com.example.watchlist.feature.ui.watchlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseStatus
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.datamodel.Item
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.repositories.IEXRepository
import com.example.watchlist.feature.repositories.TastyworksRepository
import kotlinx.coroutines.*

class WatchlistViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    val state = MutableLiveData<State>()
    val error = MutableLiveData<String>()

    val watchlist = ArrayList<String>()

    private val quotesMap = LinkedHashMap<String, Quote>()
    val quotesMapLiveData = MutableLiveData<LinkedHashMap<String, Quote>>()

    val searchResults = ArrayList<Item>()
    val searchResultsLiveData = MutableLiveData<ArrayList<Item>>()

    private val fetchQuotesJob = fetchQuotesJob()

    init {
        state.postValue(State.Loading)
        watchlist.add("AAPL")
        watchlist.add("MSFT")
        watchlist.add("GOOG")
        fetchQuotesJob.start()
    }

    private fun fetchQuotesJob(): Job {
        return viewModelScope.launch {
            while (isActive) {
                val apiCalls = mutableListOf<Deferred<Unit>>()
                watchlist.forEach {
                    apiCalls.add(async { fetchQuote(it) })
                }
                //Wait for all calls to return
                apiCalls.awaitAll()
                state.postValue(State.Done)
                quotesMapLiveData.postValue(quotesMap)

                delay(5000)
            }
        }
    }

    private suspend fun fetchQuote(symbol: String) {
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

                    state.postValue(State.Done)
                    //quote.postValue(quoteResource.data)
                }

                ResponseStatus.ERROR -> {
                    handleFailure(symbolsResource)
                }
            }
        }
    }


    private fun handleFailure(resource: Resource<Any>) {
        state.postValue(State.Error)
        error.postValue(resource.message)
    }

    override fun onCleared() {
        super.onCleared()
        fetchQuotesJob.cancel()
    }
}