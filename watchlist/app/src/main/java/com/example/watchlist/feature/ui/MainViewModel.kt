package com.example.watchlist.feature.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseStatus
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.repositories.IEXRepository
import com.example.watchlist.feature.repositories.TastyworksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    val state = MutableLiveData<State>()
    val error = MutableLiveData<String>()
    val watchlist = ArrayList<Quote>()
    val watchlistLiveData = MutableLiveData<ArrayList<Quote>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(State.Loading)
            listOf(
                async { fetchQuote("AAPL") },
                async { fetchQuote("MSFT") },
                async { fetchQuote("GOOG") }
            ).awaitAll()
            state.postValue(State.Done)
            watchlistLiveData.postValue(watchlist)

        }


    }

    suspend fun fetchQuote(symbol: String) {

        val quoteResource = IEXRepository().getQuote(symbol)
        when (quoteResource.responseStatus) {
            ResponseStatus.SUCCESS -> {
                quoteResource.data?.let { watchlist.add(it) }
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
}