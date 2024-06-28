package com.vag.lmsapp.app.joborders.preview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.moshi.JsonAdapter
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.joborders.JobOrderItemMinimal
import com.vag.lmsapp.app.joborders.cancel.JobOrderCancelActivity
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.gallery.PictureAdapter
import com.vag.lmsapp.app.joborders.gallery.JobOrderGalleryBottomSheetFragment
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity
import com.vag.lmsapp.app.joborders.payment.preview.PaymentPreviewActivity
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity
import com.vag.lmsapp.databinding.FragmentBottomSheetJobOrderPreviewBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.setGridLayout
import com.vag.lmsapp.util.showMessageDialog
import com.vag.lmsapp.util.toUUID
import java.util.UUID

class JobOrderPreviewBottomSheetFragment : BaseModalFragment() {
    override var fullHeight: Boolean = true
    private lateinit var binding: FragmentBottomSheetJobOrderPreviewBinding
    private val viewModel: JobOrderPreviewViewModel by activityViewModels()
    private val authLauncher = FragmentLauncher(this)
    private val launcher = FragmentLauncher(this)
    private val servicesAdapter = Adapter<JobOrderItemMinimal>(R.layout.recycler_item_job_order_item_minimal)
    private val productsAdapter = Adapter<JobOrderItemMinimal>(R.layout.recycler_item_job_order_item_minimal)
    private val extrasAdapter = Adapter<JobOrderItemMinimal>(R.layout.recycler_item_job_order_item_minimal)
    private lateinit var adapter: PictureAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetJobOrderPreviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = PictureAdapter(requireContext())

        binding.recyclerJobOrderGallery.adapter = adapter

        binding.recyclerJobOrderGallery.setGridLayout(requireContext(), 30.dp)

        binding.recyclerViewServices.adapter = servicesAdapter
        binding.recyclerViewProducts.adapter = productsAdapter
        binding.recyclerViewExtras.adapter = extrasAdapter

        arguments?.getBoolean(STATE_PREVIEW_ONLY)?.let {
            viewModel.setPreviewOnly(it)
        }
        arguments?.getString(JOB_ORDER_ID).toUUID()?.let {
            viewModel.getByJobOrderId(it)
        }

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is JobOrderPreviewViewModel.NavigationState.InitiateEdit -> {
                    val intent = Intent(context, JobOrderCreateActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
                        putExtra(JOB_ORDER_ID, it.id.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is JobOrderPreviewViewModel.NavigationState.MakePayment -> {
                    val intent =Intent(context, JobOrderPaymentActivity::class.java).apply {
                        action = JobOrderPaymentActivity.ACTION_LOAD_BY_CUSTOMER
                        putExtra(CUSTOMER_ID, it.customerId.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is JobOrderPreviewViewModel.NavigationState.OpenPayment -> {
                    val intent = Intent(context, PaymentPreviewActivity::class.java).apply {
                        putExtra(PAYMENT_ID, it.paymentId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                is JobOrderPreviewViewModel.NavigationState.RequestEdit -> {
                    requestAuthorization(ACTION_REQUEST_UNLOCK)
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
                is JobOrderPreviewViewModel.NavigationState.OpenDelete -> {
                    val intent = Intent(context, JobOrderCancelActivity::class.java).apply {
                        putExtra(JOB_ORDER_ID, it.id.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
//                is JobOrderPreviewViewModel.NavigationState.OpenPictures -> {
//                    openPreview(it.ids, it.position)
//                    viewModel.resetState()
//                }
                is JobOrderPreviewViewModel.NavigationState.OpenGallery -> {
                    JobOrderGalleryBottomSheetFragment.newInstance(it.jobOrderId).show(parentFragmentManager, null)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
        viewModel.jobOrderPictures.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
        viewModel.jobOrder.observe(viewLifecycleOwner, Observer {
            it?.services?.filter { !it.deleted && it.jobOrderPackageId == null }?.map { JobOrderItemMinimal(it.quantity, it.label(), it.discountedPrice) }?.let { services ->
                servicesAdapter.setData(services)
            }
            it?.products?.filter { !it.deleted && it.jobOrderPackageId == null }?.map { JobOrderItemMinimal(it.quantity, it.productName, it.discountedPrice) }?.let { products ->
                productsAdapter.setData(products)
            }
            it?.extras?.filter { !it.deleted && it.jobOrderPackageId == null }?.map { JobOrderItemMinimal(it.quantity, it.extrasName, it.discountedPrice) }?.let { extras ->
                extrasAdapter.setData(extras)
            }
//            it?.let {
//                viewModel.test(it)
//            }
        })
//        viewModel.joText.observe(viewLifecycleOwner, Observer {
//            viewModel.test(it)
//        })
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonEdit.setOnClickListener {
            viewModel.requestEdit()
        }
        binding.buttonPayment.setOnClickListener {
            viewModel.openPayment()
        }
        binding.buttonPrint.setOnClickListener {
            viewModel.openPrint()
        }
        binding.buttonDelete.setOnClickListener {
            requestAuthorization(ACTION_REQUEST_DELETE)
        }
        binding.labelLocked.setOnClickListener {
            context?.let {
                it.showMessageDialog(
                    "Job order is locked!",
                    it.getString(R.string.locked_job_order_prompt)
                )
            }
        }
        binding.jobOrderGallery.setOnClickListener {
            viewModel.openGallery()
        }

        authLauncher.onOk = {
            if(it.data?.action == ACTION_REQUEST_UNLOCK) {
                viewModel.openJobOrder()
            } else if(it.data?.action == ACTION_REQUEST_DELETE) {
                viewModel.openDelete()
            }
        }

        launcher.onOk = {
            if(it.data?.action == JobOrderCancelActivity.ACTION_DELETE_JOB_ORDER) {
                dismiss()
            }
            viewModel.requireRefresh()
        }

        adapter.onItemClick = {
            viewModel.openGallery()
        }

//        adapter.onItemClick = {
//            if(it.fileDeleted) {
//                requireContext().showDeleteConfirmationDialog("File deleted or corrupted", "Delete this file permanently?") {
//                    viewModel.removePicture(it.id)
//                }
//            } else {
//                viewModel.openPictures(it.id)
//            }
//        }
    }

//    private fun openPreview(uriIds: List<PhotoItem>, index: Int) {
//        val intent = Intent(context, PicturePreviewActivity::class.java).apply {
//            putParcelableArrayListExtra(PicturePreviewActivity.FILENAME_IDS_EXTRA, ArrayList(uriIds))
//            putExtra(PicturePreviewActivity.INDEX, index)
//        }
//        startActivity(intent)
//    }
    private fun requestAuthorization(authAction: String) {
        val intent = Intent(context, AuthActionDialogActivity::class.java).apply {
            action = authAction
            putExtra(AuthActionDialogActivity.MESSAGE, "Authentication Required")
            putExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, arrayListOf(EnumActionPermission.MODIFY_JOB_ORDERS))
        }
        authLauncher.launch(intent)
        viewModel.resetState()
    }

    companion object {
        const val ACTION_REQUEST_UNLOCK = "request_unlock"
        const val ACTION_REQUEST_DELETE = "request_delete"
        private const val STATE_PREVIEW_ONLY = "read_only"
        fun newInstance(previewOnly: Boolean, jobOrderId: UUID) : JobOrderPreviewBottomSheetFragment {
            return JobOrderPreviewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(STATE_PREVIEW_ONLY, previewOnly)
                    putString(JOB_ORDER_ID, jobOrderId.toString())
                }
            }
        }
    }
}