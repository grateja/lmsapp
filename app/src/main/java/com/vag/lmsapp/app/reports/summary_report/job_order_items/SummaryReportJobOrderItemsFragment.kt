package com.vag.lmsapp.app.reports.summary_report.job_order_items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.databinding.FragmentSummaryReportJobOrderItemsBinding
import com.vag.lmsapp.model.EnumServiceType

class SummaryReportJobOrderItemsFragment : Fragment() {
    private lateinit var binding: FragmentSummaryReportJobOrderItemsBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryReportJobOrderItemsBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.jobOrderWashItems.observe(viewLifecycleOwner, Observer {
            binding.textCaptionWashes.text = resources.getQuantityString(R.plurals.washes, it.count, it.count)
        })

        viewModel.jobOrderDryItems.observe(viewLifecycleOwner, Observer {
            binding.textCaptionDries.text = resources.getQuantityString(R.plurals.dries, it.count, it.count)
        })

        viewModel.jobOrderExtrasItems.observe(viewLifecycleOwner, Observer {
            binding.textCaptionExtras.text = resources.getQuantityString(R.plurals.extras, it.count, it.count)
        })

        viewModel.jobOrderProductsItems.observe(viewLifecycleOwner, Observer {
            binding.textCaptionProducts.text = resources.getQuantityString(R.plurals.products, it.count, it.count)
        })

        viewModel.jobOrderDeliveryItems.observe(viewLifecycleOwner, Observer {
            binding.textCaptionDelivery.text = resources.getQuantityString(R.plurals.pickup_deliveries, it.count, it.count)
        })

        binding.cardWashes.setOnClickListener {
            viewModel.openServices(EnumServiceType.WASH)
        }
        binding.cardDries.setOnClickListener {
            viewModel.openServices(EnumServiceType.DRY)
        }
        binding.cardExtras.setOnClickListener {
            viewModel.openExtras()
        }
        binding.cardProducts.setOnClickListener {
            viewModel.openProducts()
        }
        binding.cardDelivery.setOnClickListener {
            viewModel.openDeliveries()
        }


        return binding.root
    }
}