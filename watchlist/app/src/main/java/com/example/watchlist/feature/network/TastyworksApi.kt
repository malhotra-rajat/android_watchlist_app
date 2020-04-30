package com.example.watchlist.feature.network

import com.example.watchlist.feature.datamodel.SymbolSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface TastyworksApi {

    @GET("/symbols/search/{query}")
    suspend fun searchSymbol(
        @Path("query") query: String
    ): SymbolSearchResponse
}