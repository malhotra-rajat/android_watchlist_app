package com.example.watchlist.feature.watchlist.domain

import com.example.watchlist.feature.watchlist.datamodels.HistoricalPrice

class HistoricalPriceItem (private val historicalPrice: HistoricalPrice) {
    val date = historicalPrice.date
    val averagePrice = ((historicalPrice.low ?: 0.0).plus((historicalPrice.high ?: 0.0)).div(2))
}