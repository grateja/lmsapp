package com.vag.lmsapp.app.customers.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity.Companion.CUSTOMER_ID
import com.vag.lmsapp.databinding.FragmentBottomSheetCustomerAddEditBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.hideKeyboard
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CustomerAddEditBottomSheetFragment : ModalFragment<CustomerMinimal?>() {
    override var fullHeight: Boolean = true
    private val viewModel: AddEditCustomerViewModel by viewModels()
    private lateinit var binding: FragmentBottomSheetCustomerAddEditBinding
//    private val launcher = FragmentLauncher(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBottomSheetCustomerAddEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getString("data").let {
            val name = arguments?.getString("name")
            viewModel.get(it.toUUID(), name)
        }
//        arguments?.getBoolean("showSearchButton")?.let {
//            viewModel.toggleSearchVisibility(it)
//        }
        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonSave.setOnClickListener {
            binding.progressBarSaving.visibility = View.VISIBLE
            it.hideKeyboard()
            viewModel.validate()
        }
//        launcher.onOk = {
//            it?.getStringExtra(CUSTOMER_ID).toUUID()?.let {
//                viewModel.replace(it)
//            }
//        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                }
                is DataState.SaveSuccess -> {
                    onOk?.invoke(CustomerMinimal(
                        it.data.id,
                        it.data.name!!,
                        it.data.crn!!,
                        it.data.address,
                        0,
                        null,
                        null
                    ))
                    viewModel.resetState()
                    dismiss()
                }
                is DataState.InvalidInput -> {
                    binding.progressBarSaving.visibility = View.INVISIBLE
                }
                is DataState.RequestExit -> {
                    if(it.promptPass) {
                        dismiss()
                    } else {
                        Toast.makeText(context, "Press back again to revert changes", Toast.LENGTH_LONG).show()
                    }
                }

                else -> {}
            }
            binding.progressBarSaving.visibility = View.INVISIBLE
        })
    }

    override fun onStart() {
        super.onStart()
        val touchOutsideView = dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
        touchOutsideView?.setOnClickListener {
            if(!it.hideKeyboard()) {
                viewModel.requestExit()
            }
        }
    }


    companion object {
        const val CUSTOMER_ID_EXTRA = "data"
        var instance: CustomerAddEditBottomSheetFragment? = null
        fun getInstance(customerId: UUID?, presetName: String?, showSearchButton: Boolean) : CustomerAddEditBottomSheetFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = CustomerAddEditBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString("data", customerId.toString())
                        putString("name", presetName)
                        putBoolean("showSearchButton", showSearchButton)
                    }
                }
            }
            return instance as CustomerAddEditBottomSheetFragment
        }
    }
}