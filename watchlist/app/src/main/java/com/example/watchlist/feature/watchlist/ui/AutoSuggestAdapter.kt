package com.example.watchlist.feature.watchlist.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import java.util.*

class AutoSuggestAdapter(context: Context, resource: Int) :
    ArrayAdapter<String?>(context, resource), Filterable {

    private val mlistData: MutableList<String>

    fun setData(list: List<String>) {
        mlistData.clear()
        mlistData.addAll(list)
    }

    override fun getCount(): Int {
        return mlistData.size
    }

    override fun getItem(position: Int): String? {
        return mlistData[position]
    }

    fun getObject(position: Int): String {
        return mlistData[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                constraint?.let {
                    filterResults.values = mlistData
                    filterResults.count = mlistData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    init {
        mlistData = ArrayList()
    }
}