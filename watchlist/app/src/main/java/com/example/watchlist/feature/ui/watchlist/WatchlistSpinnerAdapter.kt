package com.example.watchlist.feature.ui.watchlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.watchlist.R
import com.example.watchlist.feature.datamodels.db.Watchlist


class WatchlistSpinnerAdapter(context: Context, resource: Int) :
    ArrayAdapter<Watchlist>(context, resource) {

    private val mWatchlists: ArrayList<Watchlist> = ArrayList()

    fun updateData (watchlists: ArrayList<Watchlist>) {
        mWatchlists.clear()
        mWatchlists.addAll(watchlists)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mWatchlists.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       return getWatchlistItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getWatchlistItemView(position, convertView, parent)
    }

    private fun getWatchlistItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_watchlist_item, parent, false)
        if (mWatchlists.isNotEmpty()) {
            val watchlist = getItem(position)
            val tvWatchlist = view.findViewById(R.id.tv_watchlist) as TextView
            tvWatchlist.text = watchlist?.watchlistName

        }
        return view
    }

    override fun getItem(position: Int): Watchlist? {
        return mWatchlists[position]
    }
}