package com.vag.lmsapp.app.reports.summary_report.unpaid_job_orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.databinding.FragmentSummaryReportUnpaidJobOrdersBinding
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilter
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.util.Constants.Companion.ADVANCED_FILTER

class SummaryReportUnpaidJobOrdersFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportUnpaidJobOrdersBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportUnpaidJobOrdersBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.unpaidJobOrders.observe(viewLifecycleOwner, Observer {
            binding.textTopCaption.text = resources.getQuantityString(R.plurals.unpaid_job_orders, it.count, it.count)
        })

        binding.cardContainer.setOnClickListener {
            openUnPaidJobOrders()
        }

        return binding.root
    }

    private fun openUnPaidJobOrders() {
        val intent = Intent(requireContext(), JobOrderListActivity::class.java).apply {
            val filter = JobOrderListAdvancedFilter(
                paymentStatus = EnumPaymentStatus.UNPAID
            )
            putExtra(ADVANCED_FILTER, filter)
        }
        startActivity(intent)
    }
}