package com.vag.lmsapp.app.joborders.payment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderPaymentBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.selectAllOnFocus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetJobOrderPaymentFragment : BaseModalFragment() {
    private val viewModel: JobOrderPaymentViewModel by activityViewModels()
    private lateinit var binding: FragmentBottomSheetJobOrderPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetJobOrderPaymentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        binding.textInputCashReceived.selectAllOnFocus()
        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonOk.setOnClickListener {
            dismiss()
        }
    }

    private fun subscribeListeners() {
        viewModel.cashlessProviders.observe(this, Observer {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.textInputCashlessProvider.setAdapter(adapter)
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isFitToContents = false
        return dialog
    }
}