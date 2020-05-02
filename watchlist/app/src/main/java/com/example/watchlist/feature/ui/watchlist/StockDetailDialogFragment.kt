package com.example.watchlist.feature.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.watchlist.databinding.DialogStockDetailBinding

class StockDetailDialogFragment : DialogFragment() {

    private val mBinding by lazy {
        DialogStockDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: WatchlistViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        val bundle = this.arguments
        val selectedSymbol = bundle?.getString("selected_symbol")

        viewModel.quotesMapLiveData.observe(this, Observer {
            mBinding.quote = it[selectedSymbol]
        })
    }
}