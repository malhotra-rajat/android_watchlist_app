package com.example.watchlist.feature.watchlist.datamodels

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
