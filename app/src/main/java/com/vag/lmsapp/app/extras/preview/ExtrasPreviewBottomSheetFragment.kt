package com.vag.lmsapp.app.extras.preview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.extras.edit.ExtrasAddEditActivity
import com.vag.lmsapp.databinding.FragmentExtrasPreviewBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ExtrasPreviewBottomSheetFragment : ModalFragment<UUID>() {
    override var fullHeight = true
    private lateinit var binding: FragmentExtrasPreviewBottomSheetBinding
    private val viewModel: ExtrasPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExtrasPreviewBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getString(ENTITY_ID).toUUID()?.let {
            viewModel.get(it)
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
        binding.buttonCardDelete.setOnClickListener {
            requireContext().showDeleteConfirmationDialog("Are you sure you want to delete this service?", "This action cannot be undone! Do you want to continue?") {
                viewModel.delete()
            }
        }
        binding.buttonCardHideToggle.setOnClickListener {
            viewModel.hideToggle()
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ExtrasPreviewViewModel.NavigationState.OpenEdit -> {
                    val intent = Intent(context, ExtrasAddEditActivity::class.java).apply {
                        putExtra(ENTITY_ID, it.serviceId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is ExtrasPreviewViewModel.NavigationState.DeleteSuccess -> {
                    onOk?.invoke(it.serviceId)
                    viewModel.resetState()
                    dismiss()
                }

                else -> {}
            }
        })
    }


    companion object {
        fun newInstance(extrasId: UUID): ExtrasPreviewBottomSheetFragment {
            return ExtrasPreviewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ENTITY_ID, extrasId.toString())
                }
            }
        }
    }
}