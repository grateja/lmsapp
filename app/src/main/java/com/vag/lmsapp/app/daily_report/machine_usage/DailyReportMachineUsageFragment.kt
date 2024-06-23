package com.vag.lmsapp.app.daily_report.machine_usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.daily_report.DailyReportViewModel
import com.vag.lmsapp.databinding.FragmentDailyReportMachineUsageBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.util.calculateSpanCount

class DailyReportMachineUsageFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportMachineUsageBinding
    private val viewModel: DailyReportViewModel by activityViewModels()
    private val adapter = DailyReportMachineUsageAdapter()

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
        val context = requireContext()

        binding.recyclerViewMachineUsages.layoutManager = GridLayoutManager(
            context, 2
        )

        viewModel.machineUsageSummary.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

        adapter.onItemClick = {
            viewModel.openMachineUsage(it)
        }

        return binding.root
    }
}