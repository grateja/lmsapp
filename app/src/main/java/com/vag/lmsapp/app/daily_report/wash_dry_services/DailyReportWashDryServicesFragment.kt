package com.vag.lmsapp.app.daily_report.wash_dry_services

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
import com.vag.lmsapp.databinding.FragmentDailyReportWashDryServicesBinding

class DailyReportWashDryServicesFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportWashDryServicesBinding
    private val viewModel: DailyReportViewModel by activityViewModels()

    private val washAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private val dryAdapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportWashDryServicesBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recyclerViewWashes.adapter = washAdapter
        binding.recyclerViewDries.adapter = dryAdapter

        viewModel.washServices.observe(viewLifecycleOwner, Observer {
            washAdapter.setData(it.map { it.toString() })
        })

        viewModel.dryServices.observe(viewLifecycleOwner, Observer {
            dryAdapter.setData(it.map { it.toString() })
        })

        return binding.root
    }
}