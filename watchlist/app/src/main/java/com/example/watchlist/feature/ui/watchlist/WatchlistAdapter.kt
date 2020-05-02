package com.example.watchlist.feature.ui.watchlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.R
import com.example.watchlist.databinding.WatchlistItemBinding
import com.example.watchlist.feature.datamodels.api.Quote

class WatchlistAdapter(val watchlistViewModel: WatchlistViewModel) :
    RecyclerView.Adapter<WatchlistAdapter.WatchlistItemViewHolder>() {

    private var quotesList = ArrayList<Quote>()

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
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            mBinding.root.setOnClickListener(this)
            mBinding.root.setOnLongClickListener(this)
        }

        fun bind(quote: Quote) {
            mBinding.quote = quote
            mBinding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            val fragmentManager = (mBinding.root.context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)
            val fragment = StockDetailDialogFragment() //here MyDialog is my custom dialog
            val bundle = Bundle()
            bundle.putString("selected_symbol", quotesList[layoutPosition].symbol)
            fragment.arguments = bundle

            fragment.show(fragmentTransaction, "dialog")
        }

        override fun onLongClick(v: View?): Boolean {
            val dialogBuilder = AlertDialog.Builder(v?.context)

            dialogBuilder.setTitle("Delete symbol from watchlist? ")
            dialogBuilder.setIcon(R.drawable.ic_delete_black_24dp)

            dialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                layoutPosition.let {
                    quotesList.removeAt(it)
                    quotesList[it].symbol?.let { symbol -> watchlistViewModel.removeSymbolFromWatchList(symbol) }
                    notifyItemRemoved(it)
                }

            }
            dialogBuilder.setNegativeButton("Cancel") { _, _ -> }
            val dialog = dialogBuilder.create()
            dialog.show()
            return true
        }
    }
}




