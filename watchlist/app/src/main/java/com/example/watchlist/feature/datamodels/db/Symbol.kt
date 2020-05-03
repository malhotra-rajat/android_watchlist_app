package com.example.watchlist.feature.datamodels.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity
data class Symbol(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val name: String,
    val watchlistId: Long
)
