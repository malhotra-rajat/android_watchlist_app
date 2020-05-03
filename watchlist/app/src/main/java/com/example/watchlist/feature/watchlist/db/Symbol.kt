package com.example.watchlist.feature.watchlist.db

import androidx.room.*

@Entity
data class Symbol(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val name: String,
    val watchlistId: Long
)
