package com.example.watchlist.feature.datamodels.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Watchlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val watchlistName: String
)
