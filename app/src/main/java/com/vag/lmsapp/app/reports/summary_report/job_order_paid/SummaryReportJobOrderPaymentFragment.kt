package com.vag.lmsapp.app.reports.summary_report.job_order_paid

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
import com.vag.lmsapp.databinding.FragmentSummaryReportJobOrderPaymentBinding

class SummaryReportJobOrderPaymentFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportJobOrderPaymentBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportJobOrderPaymentBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewPayments.adapter = adapter

        viewModel.jobOrderPayment.observe(viewLifecycleOwner, Observer {
            adapter.setData(it.map {it.toString()})
        })
        viewModel.jobOrderPaymentSummary.observe(viewLifecycleOwner, Observer {
            binding.textTopCaption.text = requireContext().resources.getQuantityString(R.plurals.payments, it.totalCount, it.totalCount)
        })

        binding.cardContainer.setOnClickListener {
            viewModel.openPayments()
        }

        return binding.root
    }
}