package com.vag.lmsapp.app.customers.list.advanced_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.customers.list.CustomersViewModel
import com.vag.lmsapp.databinding.FragmentCustomerListFilterBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DataState

class CustomerListFilterBottomSheetFragment : BaseModalFragment() {
    private lateinit var binding: FragmentCustomerListFilterBottomSheetBinding
    private val viewModel: CustomersViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerListFilterBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListener()

        return binding.root
    }

    private fun subscribeListener() {
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }
}