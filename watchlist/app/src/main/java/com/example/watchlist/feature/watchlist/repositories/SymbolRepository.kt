package com.example.watchlist.feature.watchlist.repositories

import androidx.lifecycle.LiveData
import com.example.watchlist.feature.watchlist.db.Symbol
import com.example.watchlist.feature.watchlist.db.SymbolDao

class SymbolRepository(private val symbolDao: SymbolDao) {

    suspend fun insert(symbol: Symbol) {
        symbolDao.insert(symbol)
    }

    suspend fun deleteById(id: Long) {
        symbolDao.deleteById(id)
    }

    fun getSymbolsForWatchlist(watchlistId: Long): LiveData<List<Symbol>> {
        return symbolDao.getSymbolsForWatchlist(watchlistId)
    }
}