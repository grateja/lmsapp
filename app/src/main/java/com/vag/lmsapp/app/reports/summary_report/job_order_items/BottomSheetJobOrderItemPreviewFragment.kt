package com.vag.lmsapp.app.reports.summary_report.job_order_items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.reports.summary_report.SummaryReportViewModel
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderItemPreviewBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.Constants.Companion.PAYLOAD

class BottomSheetJobOrderItemPreviewFragment : BaseModalFragment() {
    override var fullHeight: Boolean = true
    private lateinit var binding: FragmentBottomSheetJobOrderItemPreviewBinding
    private val viewModel: SummaryReportViewModel by activityViewModels()
    private val adapter = Adapter<SummaryReportJobOrderItemDetails>(R.layout.recycler_item_summary_report_job_order_item_preview)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetJobOrderItemPreviewBinding.inflate(
            inflater, container, false
        )

        binding.recyclerItemDetails.adapter = adapter

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        viewModel.jobOrderItemDetails.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

        arguments?.getString(PAYLOAD)?.let {
            binding.textTitle.text = it
        }

        return binding.root
    }

    companion object {
        fun newInstance(title: String): BottomSheetJobOrderItemPreviewFragment {
            return BottomSheetJobOrderItemPreviewFragment().apply {
                arguments = Bundle().apply {
                    putString(PAYLOAD, title)
                }
            }
        }
    }
}