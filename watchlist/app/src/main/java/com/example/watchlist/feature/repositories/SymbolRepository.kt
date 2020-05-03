package com.example.watchlist.feature.repositories

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.watchlist.feature.datamodels.db.Symbol
import com.example.watchlist.feature.datamodels.db.Watchlist
import com.example.watchlist.feature.domain.dao.SymbolDao
import com.example.watchlist.feature.domain.dao.WatchlistDao

class SymbolRepository(private val symbolDao: SymbolDao) {

    suspend fun insert(symbol: Symbol) {
        symbolDao.insert(symbol)
    }

    suspend fun delete(symbol: Symbol) {
        symbolDao.delete(symbol)
    }

//    fun getAll(): LiveData<List<Symbol>> {
//        return symbolDao.getAll()
//    }

    fun getSymbolsForWatchlist(watchlistId: Long): List<Symbol> {
        return symbolDao.getSymbolsForWatchlist(watchlistId)
    }
}