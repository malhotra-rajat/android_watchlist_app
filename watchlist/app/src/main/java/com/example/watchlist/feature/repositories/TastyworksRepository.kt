package com.example.watchlist.feature.repositories

import com.example.watchlist.common.network.NetworkManager
import com.example.watchlist.common.network.Resource
import com.example.watchlist.common.network.ResponseHandler
import com.example.watchlist.feature.datamodel.SymbolSearchResponse
import com.example.watchlist.feature.network.TastyworksApi

class TastyworksRepository() {


    private val responseHandler = ResponseHandler()

    private val tastyworksApi = NetworkManager.tastyWorksClient.create(TastyworksApi::class.java)

    suspend fun searchSymbol(query: String): Resource<SymbolSearchResponse> {
        return try {
            val response = tastyworksApi.searchSymbol(query)
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}