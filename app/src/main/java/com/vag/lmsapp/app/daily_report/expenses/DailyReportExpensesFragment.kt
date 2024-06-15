package com.vag.lmsapp.app.daily_report.expenses

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
import com.vag.lmsapp.databinding.FragmentDailyReportExpensesBinding

class DailyReportExpensesFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportExpensesBinding
    private val viewModel: DailyReportViewModel by activityViewModels()
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportExpensesBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewExpenses.adapter = adapter

        viewModel.expenses.observe(viewLifecycleOwner, Observer {
            adapter.setData(it.map { it.toString() })
        })

        return binding.root
    }
}