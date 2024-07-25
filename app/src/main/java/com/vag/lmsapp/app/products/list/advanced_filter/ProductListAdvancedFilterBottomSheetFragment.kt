package com.vag.lmsapp.app.products.list.advanced_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.databinding.FragmentProductListAdvancedFilterBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DataState

class ProductListAdvancedFilterBottomSheetFragment : ModalFragment<ProductListAdvancedFilter>() {
    private lateinit var binding: FragmentProductListAdvancedFilterBottomSheetBinding
    private val viewModel: ProductListAdvancedFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListAdvancedFilterBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getParcelable<ProductListAdvancedFilter>(PAYLOAD).let {
            viewModel.setInitialFilters(it)
        }

        return binding.root
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Submit -> {
                    onOk?.invoke(it.data!!)
                    dismiss()
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonApply.setOnClickListener {
            viewModel.submit()
        }
    }

    companion object {
        fun newInstance(model: ProductListAdvancedFilter): ProductListAdvancedFilterBottomSheetFragment {
            return ProductListAdvancedFilterBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, model)
                }
            }
        }
    }
}