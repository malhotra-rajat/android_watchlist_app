package com.example.watchlist.feature.watchlist.network

import com.example.watchlist.feature.watchlist.datamodels.SymbolSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface TastyworksApi {

    @GET("/symbols/search/{query}")
    suspend fun searchSymbol(
        @Path("query") query: String
    ): SymbolSearchResponse
}