package com.example.watchlist.feature.network

import com.example.watchlist.feature.datamodels.api.HistoricalPrice
import com.example.watchlist.feature.datamodels.api.Quote
import retrofit2.http.GET
import retrofit2.http.Path


interface IEXApi {

    @GET("/stable/stock/{symbol}/quote")
    suspend fun getQuote(
        @Path("symbol") symbol: String
    ): Quote

    @GET("/stable/stock/{symbol}/chart/{range}")
    suspend fun getHistoricalPrices(
        @Path("symbol") symbol: String,
        @Path("range") range: String
    ): ArrayList<HistoricalPrice>
}