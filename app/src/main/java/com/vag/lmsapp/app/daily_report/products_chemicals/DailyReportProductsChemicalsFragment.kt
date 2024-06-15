package com.vag.lmsapp.app.daily_report.products_chemicals

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
import com.vag.lmsapp.databinding.FragmentDailyReportProductsChemicalsBinding

class DailyReportProductsChemicalsFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportProductsChemicalsBinding
    private val viewModel: DailyReportViewModel by activityViewModels()

    private val detergentsAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private val fabConsAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private val othersAdapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportProductsChemicalsBinding.inflate(
            inflater, container, false
        )

        binding.recyclerViewDetergents.adapter = detergentsAdapter
        binding.recyclerViewFabCons.adapter = fabConsAdapter
        binding.recyclerViewOthers.adapter = othersAdapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.detergents.observe(viewLifecycleOwner, Observer {
            detergentsAdapter.setData(it.map { it.toString() })
        })

        viewModel.fabCons.observe(viewLifecycleOwner, Observer {
            fabConsAdapter.setData(it.map { it.toString() })
        })

        viewModel.otherProducts.observe(viewLifecycleOwner, Observer {
            othersAdapter.setData(it.map { it.toString() })
        })

        return binding.root
    }
}