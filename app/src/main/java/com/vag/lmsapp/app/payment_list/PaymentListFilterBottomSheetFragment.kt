package com.vag.lmsapp.app.payment_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.FragmentPaymentListFilterBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.model.JobOrderPaymentAdvancedFilter
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.viewmodels.ListViewModel

class PaymentListFilterBottomSheetFragment : ModalFragment<JobOrderPaymentAdvancedFilter>() {
    private lateinit var binding: FragmentPaymentListFilterBottomSheetBinding
    private val viewModel: PaymentListFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentListFilterBottomSheetBinding.inflate(
            inflater, container, true
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Submit -> {
                    onOk?.invoke(it.data!!)
                    viewModel.clearState()
                }

                is DataState.ShowDateRangePicker -> {
                    showDatePicker(it.dateFilter)
                    viewModel.clearState()
                }
                else -> {}
            }
        })

        arguments?.getParcelable<JobOrderPaymentAdvancedFilter>(PAYLOAD)?.let {
            viewModel.setInitialFilters(it)
        }

        binding.buttonApply.setOnClickListener {
            viewModel.submit()
            dismiss()
        }

        binding.buttonSelectDateRange.setOnClickListener {
            viewModel.showDateFilter()
        }

        binding.buttonClearDateFilter.setOnClickListener {
            viewModel.setDateRange(null)
        }

        return binding.root
    }

    private fun showDatePicker(dateFilter: DateFilter?) {
        val dateRangeDialog = BottomSheetDateRangePickerFragment.getInstance(dateFilter)
        dateRangeDialog.show(parentFragmentManager, null)
        dateRangeDialog.onOk = {
            viewModel.setDateRange(it)
        }
    }

    companion object {
        fun newInstance(filter: JobOrderPaymentAdvancedFilter): PaymentListFilterBottomSheetFragment {
            return PaymentListFilterBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, filter)
                }
            }
        }
    }
}