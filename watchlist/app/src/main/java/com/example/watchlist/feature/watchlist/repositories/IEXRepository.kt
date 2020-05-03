package com.example.watchlist.feature.watchlist.repositories

import com.example.watchlist.common.network.NetworkManager
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseHandler
import com.example.watchlist.feature.watchlist.datamodels.Quote
import com.example.watchlist.feature.watchlist.domain.HistoricalPriceItem
import com.example.watchlist.feature.watchlist.network.IEXApi
import java.lang.Exception

class IEXRepository {

    private val responseHandler = ResponseHandler()

    private val iexApi = NetworkManager.iexClient.create(IEXApi::class.java)

    suspend fun getQuote(symbol: String): Resource<Quote> {
        return try {
            val response = iexApi.getQuote(symbol)
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)

        }
    }

    suspend fun getHistoricalPrices(symbol: String, range: String): Resource<ArrayList<HistoricalPriceItem>> {
        return try {
            val response = iexApi.getHistoricalPrices(symbol, range)
            return responseHandler.handleSuccess(response.map { HistoricalPriceItem(it) } as ArrayList)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}