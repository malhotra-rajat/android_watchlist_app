package com.example.watchlist.feature.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseStatus
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.repositories.IEXRepository
import com.example.watchlist.feature.repositories.TastyworksRepository
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    val state = MutableLiveData<State>()
    val error = MutableLiveData<String>()
    val quotesMap = LinkedHashMap<String, Quote>()
    val quotesLiveData = MutableLiveData<LinkedHashMap<String, Quote>>()
    val watchlist = ArrayList<String>()
    val fetchQuotesJob = fetchQuotesJob()

    init {
        watchlist.add("AAPL")
        watchlist.add("MSFT")
        watchlist.add("GOOG")
        fetchQuotesJob.start()
    }

    fun fetchQuotesJob(): Job {
        return viewModelScope.launch {
            while (isActive) {
                state.postValue(State.Loading)
                val apiCalls = mutableListOf<Deferred<Unit>>()
                watchlist.forEach {
                    apiCalls.add(async { fetchQuote(it) })
                }
                //Wait for all calls to return
                apiCalls.awaitAll()
                state.postValue(State.Done)
                quotesLiveData.postValue(quotesMap)

                delay(5000)
            }
        }
    }

    suspend fun fetchQuote(symbol: String) {
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
            val symbolsResource = TastyworksRepository().searchSymbol("Goo")
            when (symbolsResource.responseStatus) {
                ResponseStatus.SUCCESS -> {
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