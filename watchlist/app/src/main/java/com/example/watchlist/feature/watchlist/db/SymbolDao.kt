package com.example.watchlist.feature.watchlist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SymbolDao {
    @Insert
    suspend fun insert(symbol: Symbol)

    @Query("Delete FROM Symbol where id=:symbolId")
    suspend fun deleteById(symbolId: Long)

    @Query("SELECT * FROM Symbol WHERE symbol.watchlistId = :watchlistId")
    fun getSymbolsForWatchlist(watchlistId: Long): LiveData<List<Symbol>>
}
