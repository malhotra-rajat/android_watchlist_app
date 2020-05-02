package com.example.watchlist.feature.datamodels.api

data class SymbolSearchResponse (
    val data: Data? = null
)

data class Data (
    val items: List<Item>? = null
)

data class Item (
    val symbol: String? = null,
    val description: String? = null
)
