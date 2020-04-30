package com.example.watchlist.feature.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchlist.R
import com.example.watchlist.databinding.ActivityMainBinding
import com.example.watchlist.feature.datamodel.Quote
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private val viewModel: MainViewModel by viewModels()

    private val watchlistAdapter by lazy {
        return@lazy WatchlistAdapter(viewModel.watchlist)
    }

    private val mBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var watchListObserver: Observer<ArrayList<Quote>> =
        Observer<ArrayList<Quote>> {
            watchlistAdapter.notifyDataSetChanged()
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


        val userNames = arrayOf("My first List", "+ Add new watchlist")
        val arrayadapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, userNames)
        mBinding.spinnerAdapter = arrayadapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                watchlistAdapter.deleteIconVisible = true
                watchlistAdapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun observeViewModel() {
        viewModel.watchlistLiveData.observe(this, watchListObserver)


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