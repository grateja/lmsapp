package com.vag.lmsapp.app.customers.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.databinding.FragmentCustomerDetailsBinding
import com.vag.lmsapp.util.showMessageDialog

class CustomerDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCustomerDetailsBinding
    private val viewModel: CustomerPreviewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonCardEdit.setOnClickListener {
            viewModel.showCustomer()
        }
        binding.buttonCreateNewJobOrder.setOnClickListener {
            if(activity?.callingActivity?.className == JobOrderCreateActivity::class.java.name) {
                activity?.finish()
            } else {
                viewModel.prepareNewJobOrder()
            }
        }

        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is CustomerPreviewViewModel.NavigationState.InvalidOperation -> {
                    context?.showMessageDialog("Cannot create new Job Order", it.message)
                    viewModel.resetState()
                }
                else -> {}
            }
        })

        return binding.root
    }
}