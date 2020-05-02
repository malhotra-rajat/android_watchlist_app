package com.example.watchlist.feature.ui.watchlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchlist.R
import com.example.watchlist.databinding.ActivityWatchlistBinding
import com.example.watchlist.feature.datamodel.Quote
import com.example.watchlist.feature.ui.showAddSymbolDialog
import com.example.watchlist.feature.ui.showAddWatchlistDialog
import kotlinx.android.synthetic.main.activity_watchlist.*


class WatchlistActivity : AppCompatActivity() {

    private val viewModel: WatchlistViewModel by viewModels()

    private val watchlistAdapter by lazy {
        return@lazy WatchlistAdapter()
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

        val userNames = arrayOf("My first List")
        val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, userNames)
        mBinding.spinnerAdapter = arrayAdapter

        supportFragmentManager
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_watchlist -> {
                showAddWatchlistDialog(this)

                true
            }

            R.id.action_edit_watchlist -> {
                watchlistAdapter.deleteIconVisible = true
                watchlistAdapter.notifyDataSetChanged()
                true
            }
            R.id.action_delete_watchlist -> {
                //delete from db
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
            autoSuggestAdapter.setData(items.map {it.symbol.toString()});
            autoSuggestAdapter.notifyDataSetChanged();
        })


        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        if (watchlistAdapter.deleteIconVisible) {
            watchlistAdapter.deleteIconVisible = false
            watchlistAdapter.notifyDataSetChanged()
        }
        else {
            super.onBackPressed()
        }
    }
}