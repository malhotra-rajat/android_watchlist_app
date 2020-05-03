package com.example.watchlist.feature.watchlist.ui

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.watchlist.R
import com.example.watchlist.feature.watchlist.db.Symbol
import com.example.watchlist.feature.watchlist.db.Watchlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun showAddWatchlistDialog(context: Context, viewModel: WatchlistViewModel) {
    val dialogBuilder = AlertDialog.Builder(context)
    val dialogView = View.inflate(context, R.layout.dialog_add_watchlist, null);
    dialogBuilder.setView(dialogView)

    val etWatchlistName = dialogView.findViewById<EditText>(R.id.et_watchlist_name)

    dialogBuilder.setPositiveButton(
        "Add"
    ) { _, _ ->
        viewModel.addWatchlist(
            Watchlist(
                watchlistName = etWatchlistName.text.toString()
            )
        )
    }

    dialogBuilder.setNegativeButton("Cancel") { _, _ -> }
    val dialog = dialogBuilder.create()
    dialog.show()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !TextUtils.isEmpty(etWatchlistName.text)

    etWatchlistName.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !TextUtils.isEmpty(s)
        }

        override fun afterTextChanged(s: Editable) {}
    }
    )
}

fun showAddSymbolDialog(
    context: Context,
    scope: CoroutineScope,
    viewModel: WatchlistViewModel,
    adapter: AutoSuggestAdapter
) {
    val dialogBuilder = AlertDialog.Builder(context)
    val dialogView = View.inflate(context, R.layout.dialog_add_symbol, null);
    dialogBuilder.setView(dialogView)

    var searchJob: Job? = null

    val autoCompleteSymbolTextView =
        dialogView.findViewById<AutoCompleteTextView>(R.id.tv_autocomplete_symbol)
    autoCompleteSymbolTextView.setAdapter(adapter)

    var selectedSymbol: String? = null

    dialogBuilder.setPositiveButton("Add Symbol to watchlist") { _, _ ->
        selectedSymbol?.let {
            if (it.isNotEmpty()) {
                val symbol = Symbol(
                    name = it,
                    watchlistId = 0
                )
                viewModel.addSymbolToWatchList(symbol)
            }
        }
    }

    dialogBuilder.setNegativeButton("Cancel") { _, _ -> }

    val dialog = dialogBuilder.create()

    dialog.show()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

    autoCompleteSymbolTextView.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            searchJob?.cancel()
            searchJob = scope.launch {

                if (!TextUtils.isEmpty(s)) {
                    delay(500)
                    viewModel.searchSymbol(s.toString())
                }
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }

        override fun afterTextChanged(s: Editable) {}
    }
    )

    autoCompleteSymbolTextView.onItemClickListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedSymbol = adapter.getObject(position)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        }
}
