package com.example.roomtutorial.ui.nonlinear

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PointLabelFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYSeries
import com.androidplot.xy.XYSeriesFormatter
import com.example.roomtutorial.databinding.FragmentNonlineardomainBinding
import com.example.roomtutorial.hrv.HRV
import com.example.roomtutorial.hrv.NonLinearDomain
import kotlin.math.pow

class NonLinearDomainFragment : Fragment() {
    private val hrv = HRV()
    private var _binding: FragmentNonlineardomainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nonLinearDomainViewModel =
            ViewModelProvider(this)[NonLinearDomainViewModel::class.java]

        _binding = FragmentNonlineardomainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        nonLinearDomainViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val numberOfSamples = 2.0.pow(4)
        val samples = context?.assets?.open("rr/000.txt")
            ?.readAllBytes()
            ?.map { it.toDouble() / 1000 }
            ?.take(numberOfSamples.toInt())

        val xySeries: XYSeries = SimpleXYSeries(
            samples?.let { hrv.nonlinearDomain(intervals = it) },
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
            "my series"
        )
        val xyFormatter = LineAndPointFormatter(Color.WHITE,Color.BLUE,Color.BLUE,null)
        binding.poincarePlot.addSeries(xySeries,xyFormatter)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}