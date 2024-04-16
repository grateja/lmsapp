package com.vag.lmsapp.app.joborders.create.shared_ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.vag.lmsapp.databinding.FragmentBottomSheetCreateJobOrderModifyQuantityBinding
import com.vag.lmsapp.fragments.ModalFragment

class CreateJobOrderModifyQuantityBottomSheetFragment : ModalFragment<QuantityModel>() {

    private lateinit var binding: FragmentBottomSheetCreateJobOrderModifyQuantityBinding
    private val quantityViewModel: QuantityViewModel by viewModels()

    var onItemRemove: ((QuantityModel) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetCreateJobOrderModifyQuantityBinding.inflate(inflater, container, false)
        val data = arguments?.getParcelable<QuantityModel>("data")
        binding.viewModel = data

        if(data?.quantity == 0) {
            binding.buttonRemove.visibility = View.GONE
        }
//        arguments?.getString("type").let {
//            if(it == QuantityModel.TYPE_PRODUCT) {
//                binding.textQuantity.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
//            } else {
//                binding.textQuantity.inputType = InputType.TYPE_CLASS_NUMBER
//            }
//            println("input type")
//            println(it)
//        }

        binding.buttonConfirm.setOnClickListener {
            if(binding.textQuantity.text.toString().toInt() > 0) {
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
                    (text.toString().toInt().plus(1)).toString()
                )
            }
        }

        binding.buttonRemove.setOnClickListener {
            onItemRemove?.invoke(binding.viewModel!!)
            dismiss()
        }

        binding.numberPickerQuantity.apply {
            minValue = 1
            maxValue = 100
            scaleX = 2f
            scaleY = 2f
        }

        return binding.root
    }

    companion object {
        fun newInstance(model: QuantityModel, type: String) : CreateJobOrderModifyQuantityBottomSheetFragment {
            return CreateJobOrderModifyQuantityBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("data", model)
                    putString("type", type)
                }
            }
//            if(instance == null || instance?.dismissed == true) {
//                instance = CreateJobOrderModifyQuantityBottomSheetFragment().apply {
//                    arguments = Bundle().apply {
//                        putParcelable("data", model)
//                        putInt("type", type)
//                    }
//                }
//            }
//            return instance as CreateJobOrderModifyQuantityBottomSheetFragment
        }
    }
}