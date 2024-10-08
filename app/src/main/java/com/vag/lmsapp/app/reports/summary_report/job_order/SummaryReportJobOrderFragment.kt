package com.vag.lmsapp.app.reports.summary_report.job_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.databinding.FragmentSummaryReportJobOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryReportJobOrderFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportJobOrderBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportJobOrderBinding.inflate(
            inflater, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.cardContainer.setOnClickListener {
            viewModel.openJobOrders()
        }

        applyTexts()

        return binding.root
    }

    private fun applyTexts() {
        val context = requireContext()
        viewModel.jobOrder.observe(viewLifecycleOwner, Observer {
            binding.textTopCaption.text = context.resources.getQuantityString(R.plurals.job_orders, it.createdCount, it.createdCount)
            binding.textJoDiscounted.text = context.resources.getQuantityString(R.plurals.discounted_job_orders, it.discountedJOCount, it.discountedJOCount)
            binding.textNewCustomer.text = context.resources.getQuantityString(R.plurals.new_customers, it.newCustomer, it.newCustomer)
            binding.textReturningCustomer.text = context.resources.getQuantityString(R.plurals.returning_customers, it.returningCustomer, it.returningCustomer)
        })
    }
}