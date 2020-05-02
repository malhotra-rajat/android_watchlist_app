package com.example.watchlist.feature.datamodels.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Symbol(
    @PrimaryKey (autoGenerate = true) val sid: Int,
    @ColumnInfo(name = "symbol") val symbol: String
)
