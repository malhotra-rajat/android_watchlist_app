package com.example.watchlist.feature.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseHandler
import com.example.watchlist.common.network.State
import com.example.watchlist.common.network.Status
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.repositories.IEXRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    val quote = MutableLiveData<Quote>()
    val state = MutableLiveData<State>()

    val error = MutableLiveData<String>()


    init {
        fetchQuote()
    }

    fun fetchQuote() {
        viewModelScope.launch(Dispatchers.IO) {
            state.postValue(State.Loading)
            val quoteResource = IEXRepository(ResponseHandler()).getQuote("AAPL")
            when (quoteResource.status) {
                Status.SUCCESS -> {
                    state.postValue(State.Done)
                    quote.postValue(quoteResource.data)
                }

                Status.ERROR -> {
                    handleFailure(quoteResource)
                }
            }
        }
    }

    private fun handleFailure(quoteResource: Resource<Quote>) {
        state.postValue(State.Error)
        error.postValue(quoteResource.message)
    }
}