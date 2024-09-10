package com.vag.lmsapp.app.reports.summary_report.machine_usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.databinding.FragmentSummaryReportMachineUsageBinding

class SummaryReportMachineUsageFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportMachineUsageBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()
    private val adapter = SummaryReportMachineUsageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportMachineUsageBinding.inflate(
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