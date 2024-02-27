package com.vag.lmsapp.app.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.vag.lmsapp.databinding.FragmentDateRangePickerBinding
import com.vag.lmsapp.util.DatePicker
import java.time.LocalDate

class DateRangePickerFragment : Fragment() {
    private val dateNavigationFromAdapter = DateNavigationAdapter()
    private val dateNavigationToAdapter = DateNavigationAdapter()

    private lateinit var binding: FragmentDateRangePickerBinding
    private val viewModel: DashboardViewModel by activityViewModels()

    private lateinit var datePicker: DatePicker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateRangePickerBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        datePicker = DatePicker(requireContext())

        binding.dateNavViewPagerFrom.adapter = dateNavigationFromAdapter
        binding.dateNavViewPagerTo.adapter = dateNavigationToAdapter

        subscribeEvents()
        subscribeListeners()

        dateNavigationFromAdapter.setCurrentDate(LocalDate.now())

        binding.dateNavViewPagerFrom.setCurrentItem(2, false)

        return binding.root
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DashboardViewModel.NavigationState.SwitchDates -> {
                    dateNavigationToAdapter.setCurrentDate(it.dateFilter.dateTo!!)
                    dateNavigationFromAdapter.setCurrentDate(it.dateFilter.dateFrom)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.dateNavViewPagerFrom.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            val handler = Handler(Looper.getMainLooper())
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dateNavigationFromAdapter.getItems()[position].let {
                    viewModel.setStartDate(it)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if(state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.post {
                        dateNavigationFromAdapter.updateItems(binding.dateNavViewPagerFrom.currentItem)
                    }
                }
            }
        })

        binding.dateNavViewPagerTo.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            val handler = Handler(Looper.getMainLooper())
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dateNavigationToAdapter.getItems()[position].let {
                    viewModel.setEndDate(it)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if(state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.post {
                        dateNavigationToAdapter.updateItems(binding.dateNavViewPagerTo.currentItem)
                    }
                }
            }
        })

        dateNavigationFromAdapter.onItemClick = {
            datePicker.show(it, "dateFrom")
        }

        dateNavigationToAdapter.onItemClick = {
            datePicker.show(it, "dateTo")
        }

        datePicker.setOnDateTimeSelectedListener { localDate, tag ->
            if(tag == "dateFrom") {
                dateNavigationFromAdapter.setCurrentDate(localDate)
                viewModel.setStartDate(localDate)
            } else if(tag == "dateTo") {
                dateNavigationToAdapter.setCurrentDate(localDate)
                viewModel.setEndDate(localDate)
                binding.dateNavViewPagerTo.setCurrentItem(2, false)
            }
        }

        binding.buttonSelectDateRange.setOnClickListener {
            datePicker.show(LocalDate.now(), "dateTo")
        }

        binding.buttonSwitchDates.setOnClickListener {
            viewModel.switchDates()
        }
    }
}