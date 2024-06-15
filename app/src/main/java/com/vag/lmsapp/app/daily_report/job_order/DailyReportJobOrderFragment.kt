package com.vag.lmsapp.app.daily_report.job_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vag.lmsapp.app.daily_report.DailyReportViewModel
import com.vag.lmsapp.databinding.FragmentDailyReportJobOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyReportJobOrderFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportJobOrderBinding
    private val viewModel: DailyReportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportJobOrderBinding.inflate(
            inflater, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}