package com.vag.lmsapp.app.joborders.create.shared_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.vag.lmsapp.databinding.FragmentBottomSheetCreateJobOrderModifyQuantityBinding
import com.vag.lmsapp.databinding.FragmentBottomSheetModifyQuantityBinding
import com.vag.lmsapp.fragments.ModalFragment

class ModifyQuantityBottomSheetFragment : ModalFragment<QuantityModel>() {

    private lateinit var binding: FragmentBottomSheetModifyQuantityBinding
    private val quantityViewModel: QuantityViewModel by viewModels()

    var onItemRemove: ((QuantityModel) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetModifyQuantityBinding.inflate(inflater, container, false)
        val data = arguments?.getParcelable<QuantityModel>("data")
        binding.viewModel = data

        if(data?.quantity == 0) {
            binding.buttonRemove.visibility = View.GONE
        }

        binding.buttonConfirm.setOnClickListener {
            if(binding.textQuantity.text.toString().toFloat() > 0f) {
                onOk?.invoke(binding.viewModel!!)
                dismiss()
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonMinus.setOnClickListener {
            binding.textQuantity.apply {
                setText(
                    (text.toString().toInt().minus(1).also {
                        binding.buttonMinus.isEnabled = it > 1
                    }).toString()
                )
            }
        }
        binding.buttonAdd.setOnClickListener {
            binding.buttonMinus.isEnabled = true
            binding.textQuantity.apply {
                setText(
                    (text.toString().toFloat().plus(1)).toString()
                )
            }
        }

        binding.buttonRemove.setOnClickListener {
            onItemRemove?.invoke(binding.viewModel!!)
            dismiss()
        }

        return binding.root
    }

    companion object {
        var instance: ModifyQuantityBottomSheetFragment? = null
        fun getInstance(model: QuantityModel) : ModifyQuantityBottomSheetFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = ModifyQuantityBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("data", model)
                    }
                }
            }
            return instance as ModifyQuantityBottomSheetFragment
        }
    }
}