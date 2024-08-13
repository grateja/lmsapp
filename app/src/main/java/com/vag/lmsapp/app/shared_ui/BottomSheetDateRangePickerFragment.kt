package com.vag.lmsapp.app.shared_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.databinding.FragmentBottomSheetDateRangePickerBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.DatePicker

class BottomSheetDateRangePickerFragment : ModalFragment<DateFilter>() {
    private lateinit var binding: FragmentBottomSheetDateRangePickerBinding
    private val viewModel: BottomSheetDateRangePickerViewModel by viewModels()
    private lateinit var datePicker: DatePicker
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDateRangePickerBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        datePicker = DatePicker(requireContext())

        subscribeListeners()
        subscribeEvents()

        arguments?.getParcelable<DateFilter>(PAYLOAD)?.let {
            viewModel.setInitialDates(it)
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.dateFrom.setOnClickListener {
            viewModel.browseDateFrom()
        }

        binding.dateTo.setOnClickListener {
            viewModel.browseDateTo()
        }

        binding.buttonApply.setOnClickListener {
            viewModel.submit()
        }

        datePicker.setOnDateTimeSelectedListener { localDate, tag ->
            if(tag == "dateFrom") {
                viewModel.setDateFrom(localDate)
            } else if(tag == "dateTo") {
                viewModel.setDateTo(localDate)
            }
        }

        binding.buttonSwitchDates.setOnClickListener {
            viewModel.switchDates()
        }

        binding.buttonClear.setOnClickListener {
            viewModel.clearTo()
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is BottomSheetDateRangePickerViewModel.NavigationState.BrowseDateFrom -> {
                    datePicker.show(it.dateFrom, "dateFrom")
                    viewModel.resetState()
                }
                is BottomSheetDateRangePickerViewModel.NavigationState.BrowseDateTo -> {
                    datePicker.show(it.dateTo, "dateTo")
                    viewModel.resetState()
                }
                is BottomSheetDateRangePickerViewModel.NavigationState.Submit -> {
                    onOk?.invoke(it.dateFilter)
                    viewModel.resetState()
                    dismiss()
                }

                else -> {}
            }
        })
    }


    companion object {
        var instance: BottomSheetDateRangePickerFragment? = null
        const val PAYLOAD = "payload"
        fun getInstance(dateFilter: DateFilter?) : BottomSheetDateRangePickerFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = BottomSheetDateRangePickerFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(PAYLOAD, dateFilter)
                    }
                }
            }
            return instance as BottomSheetDateRangePickerFragment
        }
    }
}