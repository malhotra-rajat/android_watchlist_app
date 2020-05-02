package com.example.watchlist.feature.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.watchlist.feature.datamodels.db.Watchlist

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    fun getAll(): LiveData<List<Watchlist>>

    @Insert
    suspend fun insert(watchlist: Watchlist)

    @Delete
    suspend fun delete(watchlist: Watchlist)
}
