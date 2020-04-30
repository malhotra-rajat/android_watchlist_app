package com.example.watchlist.feature.network

import com.example.watchlist.feature.datamodel.Quote
import retrofit2.http.GET
import retrofit2.http.Path


interface IEXApi {

    @GET("/stable/stock/{symbol}/quote")
    suspend fun getQuote(
        @Path("symbol") symbol: String
    ): Quote
}