package com.vag.lmsapp.app.joborders.create.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.vag.lmsapp.databinding.FragmentBottomSheetCreateJobOrderSelectDeliveryBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.model.EnumDeliveryOption

class CreateJobOrderSelectDeliveryBottomSheetFragment : BaseModalFragment() {
    private lateinit var binding: FragmentBottomSheetCreateJobOrderSelectDeliveryBinding
    private val viewModel: DeliveryViewModel by activityViewModels()

    var onOk: (() -> Unit) ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetCreateJobOrderSelectDeliveryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.quantity.maxValue = 100
        binding.quantity.minValue = 1

        binding.cardPickupOnly.setOnClickListener {
            viewModel.setDeliveryOption(EnumDeliveryOption.PICKUP_ONLY)
        }

        binding.cardDeliveryOnly.setOnClickListener {
            viewModel.setDeliveryOption(EnumDeliveryOption.DELIVERY_ONLY)
        }

        binding.cardPickupAndDelivery.setOnClickListener {
            viewModel.setDeliveryOption(EnumDeliveryOption.PICKUP_AND_DELIVERY)
        }

        binding.buttonConfirm.setOnClickListener {
            onOk?.invoke()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
    companion object {
        var instance: CreateJobOrderSelectDeliveryBottomSheetFragment? = null
        fun newInstance() : CreateJobOrderSelectDeliveryBottomSheetFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = CreateJobOrderSelectDeliveryBottomSheetFragment()
            }
            return instance as CreateJobOrderSelectDeliveryBottomSheetFragment
        }
    }
}