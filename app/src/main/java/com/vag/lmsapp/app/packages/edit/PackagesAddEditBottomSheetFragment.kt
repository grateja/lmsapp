package com.vag.lmsapp.app.packages.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.databinding.FragmentPackagesAddEditBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.room.entities.EntityPackage
import com.vag.lmsapp.util.Constants.Companion.PACKAGE_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class PackagesAddEditBottomSheetFragment : ModalFragment<EntityPackage?>() {
    override var fullHeight: Boolean = true
    private lateinit var binding: FragmentPackagesAddEditBottomSheetBinding
    private val viewModel: PackagesAddEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackagesAddEditBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getString(PACKAGE_ID).toUUID().let {
            viewModel.get(it)
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonCardClose.setOnClickListener {
            dismiss()
        }
        binding.buttonCardConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.buttonCardDelete.setOnClickListener {
            requireContext().showDeleteConfirmationDialog {
                viewModel.confirmDelete(null)
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.DeleteSuccess -> {
                    dismiss()
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    dismiss()
                }

                else -> {}
            }
        })
    }

    companion object {
        fun newInstance(packageId: UUID?): PackagesAddEditBottomSheetFragment {
            val args = Bundle().apply {
                putString(PACKAGE_ID, packageId.toString())
            }

            val fragment = PackagesAddEditBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}