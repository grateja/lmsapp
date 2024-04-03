package com.vag.lmsapp.app.joborders.create.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.list.JobOrderListItem
import com.vag.lmsapp.databinding.FragmentSelectCustomerPreviewBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.showConfirmationDialog
import com.vag.lmsapp.util.showMessageDialog

class SelectCustomerPreviewBottomSheetFragment : BaseModalFragment() {
    private lateinit var binding: FragmentSelectCustomerPreviewBottomSheetBinding
    private val viewModel: SelectCustomerViewModel by activityViewModels()
    override var fullHeight: Boolean = true
    private val adapter = Adapter<JobOrderListItem>(R.layout.recycler_item_job_order_list_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCustomerPreviewBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerUnpaidJobOrders.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeListeners() {
        viewModel.jobOrders.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }

    private fun subscribeEvents() {
        binding.cardConfirm.setOnClickListener {
            viewModel.selectCustomer()
        }
        binding.cardEditCustomerInfo.setOnClickListener {
            viewModel.editCustomer()
            dismiss()
        }
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        adapter.onItemClick = {
            if(it.locked) {
                context?.showMessageDialog("Job order is locked", "Only job orders created today can be edited!")
            } else {
                context?.showConfirmationDialog("Load this job order?", "If you have unsaved Job Orders, it will be overridden!") {
                    viewModel.selectJobOrder(it.id)
                }
            }
        }
    }
}