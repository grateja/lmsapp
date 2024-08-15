package com.vag.lmsapp.app.packages.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.packages.list.PackageItem
import com.vag.lmsapp.app.packages.preview.modify_item.PackagePreviewSelectedItemViewModel
import com.vag.lmsapp.databinding.FragmentPackagePreviewSelectItemBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.Constants.Companion.PAYLOAD
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.selectAllOnFocus
import com.vag.lmsapp.util.showDeleteConfirmationDialog

class PackagePreviewSelectItemBottomSheetFragment : BaseModalFragment() {
    override var fullHeight: Boolean = false

    var onOk: ((PackageItem) -> Unit)? = null

    private val previewViewModel: PackagesPreviewViewModel by activityViewModels()
    private val viewModel: PackagePreviewSelectedItemViewModel by viewModels()
    private lateinit var binding: FragmentPackagePreviewSelectItemBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackagePreviewSelectItemBottomSheetBinding.inflate(
            inflater, container, true
        )

        binding.viewModel = viewModel
        binding.previewViewModel = previewViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.textPrice.selectAllOnFocus()
        binding.textQuantity.selectAllOnFocus()

        subscribeEvents()
        subscribeListener()

        arguments?.getParcelable<PackageItem>(PAYLOAD)?.let {
            viewModel.setPackageItem(it)
            binding.buttonCardRemove.visibility = if(it.deleted) {View.GONE} else {View.VISIBLE}
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.buttonCardClose.setOnClickListener {
            dismiss()
        }
        binding.buttonCardRemove.setOnClickListener {
            viewModel.remove()
//            requireContext().showDeleteConfirmationDialog("Remove this item from package?") {
//            }
        }
    }

    private fun subscribeListener() {
        previewViewModel.selectedPackageItem.observe(viewLifecycleOwner, Observer {
            binding.textPrice.setText(it.price.toString())
            binding.textQuantity.setText(it.quantity.toString())
        })
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    onOk?.invoke(it.data)
                    viewModel.resetState()
                    dismiss()
                }

                is DataState.DeleteSuccess -> {
                    onOk?.invoke(it.data)
                    viewModel.resetState()
                    dismiss()
                }
                else -> {}
            }
        })
    }

    companion object {
        fun newInstance(packageItem: PackageItem): PackagePreviewSelectItemBottomSheetFragment {
            return PackagePreviewSelectItemBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, packageItem)
                }
            }
        }
    }
}