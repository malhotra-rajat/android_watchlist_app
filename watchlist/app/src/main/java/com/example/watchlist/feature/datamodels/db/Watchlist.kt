package com.example.watchlist.feature.datamodels.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Watchlist(
    @PrimaryKey(autoGenerate = true) val wid: Int = 0,
    @ColumnInfo(name = "watchlist_name") val watchlistName: String
)
