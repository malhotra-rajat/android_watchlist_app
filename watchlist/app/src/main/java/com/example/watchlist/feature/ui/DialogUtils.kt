package com.example.watchlist.feature.ui

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.example.watchlist.R
import com.example.watchlist.common.ui.State
import com.example.watchlist.feature.ui.watchlist.AutoSuggestAdapter
import com.example.watchlist.feature.ui.watchlist.WatchlistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun showAddWatchlistDialog(context: Context) {
    val dialogBuilder = AlertDialog.Builder(context)
    val dialogView = View.inflate(context, R.layout.dialog_add_watchlist, null);
    dialogBuilder.setView(dialogView)

    dialogBuilder.setPositiveButton(
        "Add"
    ) { _, _ ->
        //do something with edt.getText().toString();

    }
    dialogBuilder.setNegativeButton("Cancel") { _, _ -> }
    val dialog = dialogBuilder.create()
    dialog.show()
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
                viewModel.watchlist.add(it)
                viewModel.state.postValue(State.Loading)
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
