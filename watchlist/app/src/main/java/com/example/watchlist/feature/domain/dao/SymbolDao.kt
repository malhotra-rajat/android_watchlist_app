package com.example.watchlist.feature.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.watchlist.feature.datamodels.db.Symbol
import com.example.watchlist.feature.datamodels.db.Watchlist

@Dao
interface SymbolDao {
    @Insert
    suspend fun insert(symbol: Symbol)

    @Delete
    suspend fun delete(symbol: Symbol)

//    @Query("SELECT * FROM Symbol")
//    fun getAll(): LiveData<List<Symbol>>


    @Query("SELECT * FROM Symbol WHERE symbol.watchlistId = :watchlistId")
    fun getSymbolsForWatchlist(watchlistId: Long): List<Symbol>
}
