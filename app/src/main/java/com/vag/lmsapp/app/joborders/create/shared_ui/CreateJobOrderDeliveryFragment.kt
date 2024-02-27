package com.vag.lmsapp.app.joborders.create.shared_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vag.lmsapp.app.joborders.create.CreateJobOrderViewModel
import com.vag.lmsapp.databinding.FragmentJobOrderCreateDeliveryBinding

class CreateJobOrderDeliveryFragment : Fragment() {
    private val viewModel: CreateJobOrderViewModel by activityViewModels()
    private lateinit var binding: FragmentJobOrderCreateDeliveryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobOrderCreateDeliveryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()

        return binding.root
    }

    private fun subscribeEvents() {
        binding.fragmentDelivery.setOnClickListener {
            viewModel.openDelivery()
        }
    }
}