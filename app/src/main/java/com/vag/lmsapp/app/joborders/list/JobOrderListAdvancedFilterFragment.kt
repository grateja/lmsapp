package com.vag.lmsapp.app.joborders.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.FragmentJobOrderListAdvancedFilterBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.model.JobOrderAdvancedFilter
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.viewmodels.AdvancedFilterViewModel

class JobOrderListAdvancedFilterFragment : ModalFragment<JobOrderAdvancedFilter>() {
    private lateinit var binding: FragmentJobOrderListAdvancedFilterBinding
    private val viewModel: JobOrderListAdvancedFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobOrderListAdvancedFilterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        closeOnTouchOutside = true

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<JobOrderAdvancedFilter>(PAYLOAD).let {
            viewModel.setInitialFilters(it)
            println("date range now")
            println(it)
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
                    onOk?.invoke(it.data as JobOrderAdvancedFilter)
                    dismiss()
                    viewModel.clearState()
                }

                else -> {}
            }
        })
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is AdvancedFilterViewModel.NavigationState.ShowDateRangePicker -> {
                    showDatePicker(it.dateFilter)
                    viewModel.clearNavigation()
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
        private var instance: JobOrderListAdvancedFilterFragment? = null
        fun getInstance(model: JobOrderAdvancedFilter): JobOrderListAdvancedFilterFragment {
            if(instance == null) {
                instance = JobOrderListAdvancedFilterFragment()
            }
            instance?.arguments = Bundle().apply {
                putParcelable(PAYLOAD, model)
            }
            return instance as JobOrderListAdvancedFilterFragment
        }
    }
}