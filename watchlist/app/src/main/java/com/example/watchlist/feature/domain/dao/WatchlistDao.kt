package com.example.watchlist.feature.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.watchlist.feature.datamodels.db.Watchlist

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM Watchlist")
    fun getAll(): LiveData<List<Watchlist>>

    @Insert
    suspend fun insert(watchlist: Watchlist)

    @Delete
    suspend fun delete(watchlist: Watchlist)
}
