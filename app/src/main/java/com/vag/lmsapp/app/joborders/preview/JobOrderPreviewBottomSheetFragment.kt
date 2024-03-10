package com.vag.lmsapp.app.joborders.preview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity.Companion.JOB_ORDER_ID
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity
import com.vag.lmsapp.app.joborders.payment.preview.PaymentPreviewActivity
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderPreviewBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.showMessageDialog

class JobOrderPreviewBottomSheetFragment : BaseModalFragment() {
    override var fullHeight: Boolean = true
    private lateinit var binding: FragmentBottomSheetJobOrderPreviewBinding
    private val viewModel: JobOrderPreviewViewModel by activityViewModels()
    private val launcher = FragmentLauncher(this)
    private val servicesAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private val productsAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private val extrasAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetJobOrderPreviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.recyclerViewServices.adapter = servicesAdapter
        binding.recyclerViewProducts.adapter = productsAdapter
        binding.recyclerViewExtras.adapter = extrasAdapter

        binding.buttonEdit.setOnClickListener {
            viewModel.requestEdit()
        }
        binding.buttonPayment.setOnClickListener {
            viewModel.openPayment()
        }
        binding.buttonPrint.setOnClickListener {
            viewModel.openPrint()
        }

        launcher.onOk = {
            if(it?.action == ACTION_REQUEST_UNLOCK) {
                viewModel.openJobOrder()
            }
        }

        viewModel.jobOrder.observe(viewLifecycleOwner, Observer {
            it?.services?.map { it.serviceName }?.let {services ->
                servicesAdapter.setData(services)
            }
            it?.products?.map { it.productName }?.let {products ->
                productsAdapter.setData(products)
            }
            it?.extras?.map { it.extrasName }?.let {extras ->
                extrasAdapter.setData(extras)
            }
        })

        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is JobOrderPreviewViewModel.NavigationState.InitiateEdit -> {
                    val intent = Intent(context, JobOrderCreateActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
                        putExtra(JOB_ORDER_ID, it.id.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                    dismiss()
                }
                is JobOrderPreviewViewModel.NavigationState.InitiatePayment -> {
                    val intent =Intent(context, JobOrderPaymentActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_SYNC_PAYMENT
                        putExtra(JobOrderPaymentActivity.CUSTOMER_ID, it.customerId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                    dismiss()
                }
                is JobOrderPreviewViewModel.NavigationState.PreviewPayment -> {
                    val intent = Intent(context, PaymentPreviewActivity::class.java).apply {
                        putExtra(JobOrderPaymentActivity.PAYMENT_ID, it.paymentId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                    dismiss()
                }
                is JobOrderPreviewViewModel.NavigationState.RequestEdit -> {
                    val intent = Intent(context, AuthActionDialogActivity::class.java).apply {
                        action = ACTION_REQUEST_UNLOCK
                        putExtra(AuthActionDialogActivity.MESSAGE, "Authentication Required")
                        putExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, arrayListOf(EnumActionPermission.MODIFY_JOB_ORDERS))
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is JobOrderPreviewViewModel.NavigationState.OpenPrint -> {
                    val intent = Intent(context, JobOrderPrintActivity::class.java).apply {
                        putExtra(Constants.ID, it.id.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                is JobOrderPreviewViewModel.NavigationState.Invalidate -> {
                    context?.showMessageDialog("Invalid operation", it.message)
                    viewModel.resetState()
                }
                else -> {}
            }
        })

        return binding.root
    }

    companion object {
        const val ACTION_REQUEST_UNLOCK = "request_unlock"
    }
}