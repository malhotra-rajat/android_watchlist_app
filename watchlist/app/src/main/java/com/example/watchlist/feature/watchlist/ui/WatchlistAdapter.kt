package com.example.watchlist.feature.watchlist.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.R
import com.example.watchlist.databinding.WatchlistItemBinding
import com.example.watchlist.feature.watchlist.datamodels.Quote
import com.example.watchlist.feature.watchlist.ui.StockDetailDialogFragment

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
           showStockDetailDialog()
        }

        override fun onLongClick(v: View?): Boolean {
            showDeleteSymbolDialog(v)
            return true
        }

        private fun showStockDetailDialog() {
            val fragmentManager =
                (mBinding.root.context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)

            val stockDetailDialogFragment = quotesList[layoutPosition].symbol?.let {
                StockDetailDialogFragment()
                    .newInstance(
                    it
                )
            }
            stockDetailDialogFragment?.show(fragmentTransaction, "dialog")
        }

        private fun showDeleteSymbolDialog(v: View?) {
            val dialogBuilder = AlertDialog.Builder(v?.context)

            dialogBuilder.setTitle("Delete symbol from watchlist? ")
            dialogBuilder.setIcon(R.drawable.ic_delete_black_24dp)

            dialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                layoutPosition.let { layoutPosition ->
                    quotesList[layoutPosition].symbol?.let {
                        val symbolId = watchlistViewModel.currentSymbolsMap[quotesList[layoutPosition].symbol]
                        if (symbolId != null) {
                            watchlistViewModel.removeSymbolFromWatchList(symbolId)
                        }
                    }
                    notifyItemRemoved(layoutPosition)
                    quotesList.removeAt(layoutPosition)
                }

            }
            dialogBuilder.setNegativeButton("Cancel") { _, _ -> }
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }
}




