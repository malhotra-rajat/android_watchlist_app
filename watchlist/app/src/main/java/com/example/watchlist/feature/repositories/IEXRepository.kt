package com.example.watchlist.feature.repositories

import com.example.watchlist.common.network.NetworkManager
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseHandler
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.network.IEXApi
import java.lang.Exception

class IEXRepository(private val responseHandler: ResponseHandler) {

    private val iexApi = NetworkManager.iexClient.create(IEXApi::class.java)
    suspend fun getQuote(symbol: String): Resource<Quote> {
        return try {
            val response = iexApi.getQuote(symbol, NetworkManager.iexAuthToken)
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)

        }
    }
}