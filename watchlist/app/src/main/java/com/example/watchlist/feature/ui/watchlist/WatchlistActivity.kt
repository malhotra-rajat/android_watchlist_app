package com.example.watchlist.feature.ui.watchlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchlist.R
import com.example.watchlist.databinding.ActivityWatchlistBinding
import com.example.watchlist.feature.datamodels.api.Quote
import com.example.watchlist.feature.datamodels.db.Watchlist
import com.example.watchlist.feature.ui.showAddSymbolDialog
import com.example.watchlist.feature.ui.showAddWatchlistDialog
import kotlinx.android.synthetic.main.activity_watchlist.*

class WatchlistActivity : AppCompatActivity() {

    private val viewModel: WatchlistViewModel by viewModels()

    private val watchlistAdapter by lazy {
        return@lazy WatchlistAdapter(viewModel)
    }

    private val mBinding by lazy {
        ActivityWatchlistBinding.inflate(layoutInflater)
    }

    private var watchListObserver = Observer<LinkedHashMap<String, Quote>> {
        watchlistAdapter.updateData(it)
    }

    private val autoSuggestAdapter by lazy {
        return@lazy AutoSuggestAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item
        )
    }

    private val watchlistSpinnerAdapter by lazy {
        return@lazy WatchlistSpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        initUI()
        observeViewModel()
    }

    private fun initUI() {
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            rv_watchlist.context,
            linearLayoutManager.orientation
        )

        rv_watchlist.apply {
            layoutManager = linearLayoutManager
            adapter = watchlistAdapter
            addItemDecoration(dividerItemDecoration)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_watchlist -> {
                showAddWatchlistDialog(this, viewModel)
                true
            }

            R.id.action_delete_watchlist -> {
                val watchlist =
                    watchlistSpinnerAdapter.getItem(spinner_watchlist.selectedItemPosition)
                watchlist?.let { viewModel.deleteWatchlist(it) }
                true
            }

            R.id.action_add_symbol -> {
                showAddSymbolDialog(this, lifecycleScope, viewModel, autoSuggestAdapter)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun observeViewModel() {
        viewModel.quotesMapLiveData.observe(this, watchListObserver)

        viewModel.searchResultsLiveData.observe(this, Observer { items ->
            autoSuggestAdapter.setData(items.map { it.symbol.toString() });
            autoSuggestAdapter.notifyDataSetChanged();
        })


        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        mBinding.spinnerAdapter = watchlistSpinnerAdapter
        viewModel.watchlists.observe(this, Observer {
            watchlistSpinnerAdapter.updateData(it as ArrayList<Watchlist>)
        })
    }
}