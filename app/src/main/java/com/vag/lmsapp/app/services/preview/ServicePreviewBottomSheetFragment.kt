package com.vag.lmsapp.app.services.preview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.services.edit.AddEditServiceActivity
import com.vag.lmsapp.databinding.FragmentServicePreviewBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.Constants.Companion.PAYLOAD
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ServicePreviewBottomSheetFragment : BaseModalFragment() {
    override var fullHeight = true
    private lateinit var binding: FragmentServicePreviewBottomSheetBinding
    private val viewModel: ServicePreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicePreviewBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getString(PAYLOAD)?.toUUID()?.let {
            viewModel.setId(it)
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonCardClose.setOnClickListener {
            dismiss()
        }
        binding.buttonCardEdit.setOnClickListener {
            viewModel.openEdit()
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ServicePreviewViewModel.NavigationState.OpenEdit -> {
                    val intent = Intent(context, AddEditServiceActivity::class.java).apply {
                        putExtra(AddEditServiceActivity.SERVICE_ID_EXTRA, it.serviceId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }

    companion object {
        fun newInstance(serviceId: UUID): ServicePreviewBottomSheetFragment {
            return ServicePreviewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(PAYLOAD, serviceId.toString())
                }
            }
        }
    }
}