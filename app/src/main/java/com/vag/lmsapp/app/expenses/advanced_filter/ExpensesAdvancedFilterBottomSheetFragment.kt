package com.vag.lmsapp.app.expenses.advanced_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.FragmentExpensesAdvancedFilterBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.DateFilter

class ExpensesAdvancedFilterBottomSheetFragment : ModalFragment<ExpensesAdvancedFilter>() {
    private lateinit var binding: FragmentExpensesAdvancedFilterBottomSheetBinding
    private val viewModel: ExpensesAdvancedFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesAdvancedFilterBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<ExpensesAdvancedFilter>(PAYLOAD).let {
            viewModel.setInitialFilters(it)
        }
    }

    private fun showDatePicker(dateFilter: DateFilter?) {
        val dateRangeDialog = BottomSheetDateRangePickerFragment.getInstance(dateFilter)
        dateRangeDialog.show(parentFragmentManager, null)
        dateRangeDialog.onOk = {
            viewModel.setDateRange(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Submit -> {
                    onOk?.invoke(it.data!!)
                    dismiss()
                    viewModel.clearState()
                }

                is DataState.ShowDateRangePicker -> {
                    showDatePicker(it.dateFilter)
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonApply.setOnClickListener {
            viewModel.submit()
        }
        binding.buttonSelectDateRange.setOnClickListener {
            viewModel.showDateFilter()
        }
        binding.buttonClearDateFilter.setOnClickListener {
            viewModel.setDateRange(null)
        }
    }

    companion object {
        fun newInstance(model: ExpensesAdvancedFilter): ExpensesAdvancedFilterBottomSheetFragment {
            return ExpensesAdvancedFilterBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, model)
                }
            }
        }
    }


}