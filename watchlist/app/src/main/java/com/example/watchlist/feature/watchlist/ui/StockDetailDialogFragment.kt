package com.example.watchlist.feature.watchlist.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.watchlist.R
import com.example.watchlist.databinding.DialogStockDetailBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.dialog_stock_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockDetailDialogFragment : DialogFragment() {

    private val mBinding by lazy {
        DialogStockDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: WatchlistViewModel by activityViewModels()

    fun newInstance(selectedSymbol: String): StockDetailDialogFragment? {
        val f =
            StockDetailDialogFragment()

        val args = Bundle()
        args.putString(SELECTED_SYMBOL, selectedSymbol)
        f.arguments = args

        return f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedSymbol = arguments?.getString(SELECTED_SYMBOL)

        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        viewModel.quotesMapLiveData.observe(this, Observer {
            mBinding.quote = it[selectedSymbol]
        })

        selectedSymbol?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getHistoricalPrices(it)
            }
        }

        stock_chart.setTouchEnabled(true)
        stock_chart.setPinchZoom(true)

        viewModel.chartEntriesLiveData.observe(this, Observer {
            val lineDataSet = LineDataSet(it, "")
            lineDataSet.color = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
            lineDataSet.valueTextColor =
                ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            val xAxis: XAxis = stock_chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            val formatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return viewModel.chartDates[value.toInt()]
                }
            }
            xAxis.granularity = 1f
            xAxis.valueFormatter = formatter
            xAxis.labelRotationAngle = -45f

            val yAxisRight: YAxis = stock_chart.axisRight
            yAxisRight.isEnabled = false

            val yAxisLeft: YAxis = stock_chart.axisLeft
            yAxisLeft.granularity = 1f

            val data = LineData(lineDataSet)
            stock_chart.data = data
            stock_chart.animateX(500)
            stock_chart.invalidate()
        })
    }

    companion object {
        private const val SELECTED_SYMBOL = "selected_symbol"
    }
}