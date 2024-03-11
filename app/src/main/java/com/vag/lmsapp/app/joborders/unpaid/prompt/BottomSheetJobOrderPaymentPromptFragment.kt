package com.vag.lmsapp.app.joborders.unpaid.prompt

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderPaymentPromptBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.showMessageDialog
import java.util.UUID

class BottomSheetJobOrderPaymentPromptFragment: BaseModalFragment() {
    override var fullHeight = true
    private lateinit var binding: FragmentBottomSheetJobOrderPaymentPromptBinding
    private val viewModel: JobOrdersUnpaidPromptViewModel by activityViewModels()
    private val adapter = Adapter<JobOrderPaymentMinimal>(R.layout.recycler_item_job_order_read_only)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetJobOrderPaymentPromptBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerUnpaidJobOrders.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonPayment.setOnClickListener {
            viewModel.openPayment()
        }
        binding.buttonCreateNew.setOnClickListener {
            viewModel.openJobOrder()
        }
        adapter.onItemClick = {
            context?.showMessageDialog("Invalid operation", "Job order is locked and cannot be edited!")
        }
    }

    private fun subscribeListeners() {
        viewModel.jobOrders.observe(this, Observer {
            adapter.setData(it)
        })
//        viewModel.dataState.observe(this, Observer {
//            when(it) {
//                is JobOrdersUnpaidPromptViewModel.DataState.OpenPayment -> {
////                    openPayment(it.customerId)
//                    viewModel.resetState()
//                }
//                is JobOrdersUnpaidPromptViewModel.DataState.OpenJobOrder -> {
////                    openCreateJobOrderActivity(it.customer)
//                    viewModel.resetState()
//                }
//
//                else -> {}
//            }
//        })
        viewModel.customer.observe(this, Observer {
//            title = "${it.name} - [${it.crn}]"
        })
    }
}