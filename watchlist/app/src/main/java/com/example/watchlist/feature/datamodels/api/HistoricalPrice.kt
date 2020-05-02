package com.example.watchlist.feature.datamodels.api

data class HistoricalPrice (
    val date: String? = null,
    val open: Double? = null,
    val close: Double? = null,
    val high: Double? = null,
    val low: Double? = null,
    val volume: Long? = null,
    val uOpen: Double? = null,
    val uClose: Double? = null,
    val uHigh: Double? = null,
    val uLow: Double? = null,
    val uVolume: Long? = null,
    val change: Double? = null,
    val changePercent: Double? = null,
    val label: String? = null,
    val changeOverTime: Double? = null
)
