package com.vag.lmsapp.app.reports.summary_report.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.databinding.FragmentSummaryReportExpensesBinding

class SummaryReportExpensesFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportExpensesBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportExpensesBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewExpenses.adapter = adapter

        viewModel.expenses.observe(viewLifecycleOwner, Observer {
            binding.textTopCaption.text = requireContext().resources.getQuantityString(R.plurals.expenses, it.size, it.size)
            adapter.setData(it.map { it.toString() })
        })

        binding.cardContainer.setOnClickListener {
            viewModel.openExpenses()
        }

        return binding.root
    }
}