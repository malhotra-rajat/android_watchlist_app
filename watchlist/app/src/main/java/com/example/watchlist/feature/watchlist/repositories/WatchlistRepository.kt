package com.example.watchlist.feature.watchlist.repositories

import androidx.lifecycle.LiveData
import com.example.watchlist.feature.watchlist.db.Watchlist
import com.example.watchlist.feature.watchlist.db.WatchlistDao

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    fun getAll(): LiveData<List<Watchlist>> {
        return watchlistDao.getAll()
    }

    suspend fun insert(watchlist: Watchlist) {
        watchlistDao.insert(watchlist)
    }

    suspend fun delete(watchlist: Watchlist) {
        watchlistDao.delete(watchlist)
    }
}