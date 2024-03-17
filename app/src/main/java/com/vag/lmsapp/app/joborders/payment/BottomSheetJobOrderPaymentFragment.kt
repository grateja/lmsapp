package com.vag.lmsapp.app.joborders.payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderPaymentBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.selectAllOnFocus
import com.vag.lmsapp.R
import com.vag.lmsapp.app.gallery.picture_browser.PictureCaptureActivity
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.util.UUID

@AndroidEntryPoint
class BottomSheetJobOrderPaymentFragment : BaseModalFragment() {
    override var fullHeight = true
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
        binding.textInputCashlessAmount.selectAllOnFocus()
        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonDone.setOnClickListener {
            dismiss()
        }
    }

    private fun subscribeListeners() {
        viewModel.cashlessProviders.observe(viewLifecycleOwner, Observer {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.textInputCashlessProvider.setAdapter(adapter)
        })
    }


//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = BottomSheetDialog(requireContext(), theme)
//        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        dialog.behavior.isFitToContents = false
//        return dialog
//    }
}