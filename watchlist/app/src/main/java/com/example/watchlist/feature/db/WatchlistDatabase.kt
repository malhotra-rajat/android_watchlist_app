package com.example.watchlist.feature.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.watchlist.feature.datamodels.db.Symbol
import com.example.watchlist.feature.datamodels.db.Watchlist
import com.example.watchlist.feature.domain.dao.SymbolDao
import com.example.watchlist.feature.domain.dao.WatchlistDao

@Database(entities = [Watchlist::class, Symbol::class], version = 1, exportSchema = false)
abstract class WatchlistDatabase : RoomDatabase() {

    abstract fun watchlistDao(): WatchlistDao
    abstract fun symbolDao(): SymbolDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WatchlistDatabase? = null

        fun getDatabase(context: Context): WatchlistDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WatchlistDatabase::class.java,
                    "watchlist_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}