package com.example.watchlist.feature.watchlist.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM Watchlist")
    fun getAll(): LiveData<List<Watchlist>>

    @Insert
    suspend fun insert(watchlist: Watchlist)

    @Delete
    suspend fun delete(watchlist: Watchlist)
}
