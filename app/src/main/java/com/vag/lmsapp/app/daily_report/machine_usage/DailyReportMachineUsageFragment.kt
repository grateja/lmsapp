package com.vag.lmsapp.app.daily_report.machine_usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.daily_report.DailyReportViewModel
import com.vag.lmsapp.databinding.FragmentDailyReportMachineUsageBinding

class DailyReportMachineUsageFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportMachineUsageBinding
    private val viewModel: DailyReportViewModel by activityViewModels()
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportMachineUsageBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewMachineUsages.adapter = adapter

        viewModel.machineUsages.observe(viewLifecycleOwner, Observer {
            adapter.setData(it.map { it.toString() })
        })

        return binding.root
    }
}