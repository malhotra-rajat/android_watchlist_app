package com.example.watchlist.common.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkManager {

    private const val TASTYWORKS_BASE_URL = "https://api.tastyworks.com"
    private const val IEX_CLOUD_BASE_URL = "https://cloud.iexapis.com"

    const val iexAuthToken = "sk_8430ddcc5baa4476853c1211084987cc"

    private val gsonFactory = GsonConverterFactory.create()
    private val okHttpClient = OkHttpClient()

    var tastyWorksClient = Retrofit.Builder()
        .baseUrl(TASTYWORKS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonFactory)
        .build()

    var iexClient: Retrofit = Retrofit.Builder()
        .baseUrl(IEX_CLOUD_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonFactory)
        .build()

}
