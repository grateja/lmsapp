package com.vag.lmsapp.app.joborders.create.shared_ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.joborders.create.CreateJobOrderViewModel
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity.Companion.ACTION_MODIFY_DATE_TIME
import com.vag.lmsapp.databinding.FragmentCreateJobOrderInfoBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherFragment
import com.vag.lmsapp.util.DateTimePicker
import com.vag.lmsapp.util.FragmentLauncher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateJobOrderInfoFragment : Fragment() {
    private val viewModel: CreateJobOrderViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateJobOrderInfoBinding

//    val launcher = AuthLauncherFragment(this)

    private val dateTimePicker: DateTimePicker by lazy {
        DateTimePicker(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateJobOrderInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeEvents() {
//        launcher.onOk = { loginCredentials, code ->
//            viewModel.requestModifyDateTime()
//        }
        binding.cardButtonCreatedAt.setOnClickListener {
            openAuthRequestModifyDateTime()
        }
        binding.cardButtonSelectCustomer.setOnClickListener {
            viewModel.pickCustomer()
        }
//        binding.buttonEdit.setOnClickListener {
//            viewModel.editCustomer()
//        }
//        binding.buttonSearch.setOnClickListener {
//            viewModel.searchCustomer()
//        }
        dateTimePicker.setOnDateTimeSelectedListener {
            viewModel.applyDateTime(it)
            Toast.makeText(context, "Date time created modified", Toast.LENGTH_SHORT).show()
        }
//        binding.cardButtonPayment.setOnClickListener {
//            viewModel.openPayment()
//        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is CreateJobOrderViewModel.DataState.ModifyDateTime -> {
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        dateTimePicker.show(it.createdAt)
                    }, 100)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun openAuthRequestModifyDateTime() {
        authViewModel.authenticate(
            listOf(EnumActionPermission.MODIFY_JOB_ORDERS), ACTION_MODIFY_DATE_TIME, false
        )
//        launcher.launch(listOf(EnumActionPermission.MODIFY_JOB_ORDERS), "Modify date time", false)
//        val intent = Intent(context, AuthActionDialogActivity::class.java).apply {
//            action = JobOrderCreateActivity.ACTION_MODIFY_DATETIME
//            putExtra(
//                AuthActionDialogActivity.PERMISSIONS_EXTRA, arrayListOf(
//                EnumActionPermission.MODIFY_JOB_ORDERS,
//                EnumActionPermission.MODIFY_SERVICES
//            ))
//        }
//        launcher.launch(intent)
    }
}