package com.example.watchlist.feature.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.databinding.WatchlistItemBinding
import com.example.watchlist.feature.datamodel.Quote

class WatchlistAdapter() :
    RecyclerView.Adapter<WatchlistAdapter.WatchlistItemViewHolder>() {

    private var quotesList = ArrayList<Quote>()
    var deleteIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: WatchlistItemBinding =
            WatchlistItemBinding.inflate(layoutInflater, parent, false)
        return WatchlistItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        holder.bind(quotesList[position])
    }

    override fun getItemCount(): Int {
        return quotesList.size
    }

    fun updateData(mQuotesMap: LinkedHashMap<String, Quote>) {
        quotesList.clear()
        quotesList.addAll(mQuotesMap.values)
        notifyDataSetChanged()
    }

    inner class WatchlistItemViewHolder(private val mBinding: WatchlistItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.btnDelete.setOnClickListener {
                layoutPosition.let {
                    quotesList.removeAt(it)
                    notifyItemRemoved(it)
                }
            }
        }

        fun bind(quote: Quote) {
            mBinding.quote = quote
            mBinding.deleteIconVisible = deleteIconVisible
            mBinding.executePendingBindings()
        }
    }

}




