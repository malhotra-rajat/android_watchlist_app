package com.example.watchlist.feature.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.databinding.WatchlistItemBinding
import com.example.watchlist.feature.datamodel.Quote

class WatchlistAdapter(private var mQuotes: ArrayList<Quote>) :
    RecyclerView.Adapter<WatchlistAdapter.WatchlistItemViewHolder>() {

    var deleteIconVisible = false;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: WatchlistItemBinding =
            WatchlistItemBinding.inflate(layoutInflater, parent, false)
        return WatchlistItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        holder.bind(mQuotes[position])
    }

    override fun getItemCount(): Int {
        return mQuotes.size
    }


    fun updateData(quotes: ArrayList<Quote>) {
        quotes.clear()
        quotes.addAll(quotes)
        notifyDataSetChanged()
    }


    inner class WatchlistItemViewHolder(private val mBinding: WatchlistItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
//            mBinding.addButton.setOnClickListener(insert())
            mBinding.btnDelete.setOnClickListener {
                layoutPosition.let {
                    mQuotes.removeAt(it)
                    notifyItemRemoved(it)
                }
            }
        }

        fun bind(quote: Quote) {
            mBinding.quote = quote
            mBinding.deleteIconVisible = deleteIconVisible
            mBinding.executePendingBindings()
        }
/*
    private fun insert(): (View) -> Unit = {
        layoutPosition.also { currentPosition ->
            items.add(currentPosition, uniqueString(string))
            notifyDataSetChanged()
        }
    }*/

    }

}




