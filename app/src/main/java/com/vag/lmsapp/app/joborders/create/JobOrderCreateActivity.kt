package com.vag.lmsapp.app.joborders.create

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.joborders.cancel.JobOrderCancelActivity
import com.vag.lmsapp.app.joborders.create.customer.JobOrderCreateSelectCustomerActivity
import com.vag.lmsapp.app.joborders.create.delivery.DeliveryCharge
import com.vag.lmsapp.app.joborders.create.delivery.JobOrderCreateSelectDeliveryActivity
import com.vag.lmsapp.app.joborders.create.discount.JobOrderCreateSelectDiscountActivity
import com.vag.lmsapp.app.joborders.create.discount.MenuDiscount
import com.vag.lmsapp.app.joborders.create.extras.JobOrderCreateSelectExtrasActivity
import com.vag.lmsapp.app.joborders.create.extras.JobOrderExtrasItemAdapter
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.app.joborders.gallery.PictureAdapter
import com.vag.lmsapp.app.joborders.create.packages.JobOrderCreateSelectPackageActivity
import com.vag.lmsapp.app.joborders.create.packages.MenuJobOrderPackage
import com.vag.lmsapp.app.joborders.create.products.JobOrderCreateSelectProductsActivity
import com.vag.lmsapp.app.joborders.create.products.JobOrderProductsItemAdapter
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.services.JobOrderCreateSelectWashDryActivity
import com.vag.lmsapp.app.joborders.create.services.JobOrderServiceItemAdapter
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.app.joborders.gallery.JobOrderGalleryBottomSheetFragment
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.app.joborders.payment.preview.PaymentPreviewActivity
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity
import com.vag.lmsapp.databinding.ActivityJobOrderCreateBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.services.JobOrderSyncService
import com.vag.lmsapp.util.*
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class JobOrderCreateActivity : BaseActivity(), InternetConnectionCallback {
    companion object {
        const val CUSTOMER_EXTRA = "customer"
        const val ACTION_LOAD_BY_CUSTOMER_ID = "loadByCustomerId"
        const val ACTION_LOAD_BY_JOB_ORDER_ID = "loadByJobOrderId"
        const val ACTION_LOAD_EMPTY_JOB_ORDER = "loadEmptyJobOrder"
        const val PAYLOAD_EXTRA = "payload"
        const val ITEM_PRESET_EXTRA = "itemPreset"

        const val ACTION_SELECT_CUSTOMER = "searchCustomer"
        const val ACTION_SYNC_PACKAGE = "package"
        const val ACTION_SYNC_SERVICES = "services"
        const val ACTION_SYNC_PRODUCTS = "products"
        const val ACTION_SYNC_EXTRAS = "extras"
        const val ACTION_SYNC_DELIVERY = "delivery"
        const val ACTION_SYNC_DISCOUNT = "discount"
        const val ACTION_DELETE_JOB_ORDER = "deleteJobOrder"
        const val ACTION_CONFIRM_SAVE = "Confirm save"
        const val ACTION_MODIFY_DATE_TIME = "Modify Date & Time"
    }

    private var internetAvailable: Boolean = false
    private lateinit var binding: ActivityJobOrderCreateBinding
    private val viewModel: CreateJobOrderViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val launcher = ActivityLauncher(this)
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private val servicesAdapter = JobOrderServiceItemAdapter()
    private val productsAdapter = JobOrderProductsItemAdapter()
    private val extrasAdapter = JobOrderExtrasItemAdapter()
    private val unpaidJobOrdersAdapter = Adapter<JobOrderPaymentMinimal>(R.layout.recycler_item_job_order_read_only)
    private val packageAdapter = Adapter<MenuJobOrderPackage>(R.layout.recycler_item_create_job_order_selected_package)
    private val pictureListAdapter = PictureAdapter(this)

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_CONFIRM_SAVE -> {
                viewModel.save(loginCredentials.userId)
            }

            ACTION_MODIFY_DATE_TIME -> {
                viewModel.requestModifyDateTime()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create)
//        window.statusBarColor = getColor(R.color.color_code_job_order)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.inclPackageLegend.recycler.adapter = packageAdapter
        binding.inclServicesLegend.recycler.adapter = servicesAdapter
        binding.inclProductsLegend.recycler.adapter = productsAdapter
        binding.inclExtrasLegend.recycler.adapter = extrasAdapter
        binding.recyclerJobOrderGallery.adapter = pictureListAdapter
        binding.recyclerJobOrderGallery.setGridLayout(this, 30.dp)

        binding.inclPackageLegend.icon.setImageResource(R.drawable.prepend_icon_package)
        binding.inclServicesLegend.icon.setImageResource(R.drawable.prepend_icon_wash_services)
        binding.inclExtrasLegend.icon.setImageResource(R.drawable.prepend_icon_extra_services)
        binding.inclProductsLegend.icon.setImageResource(R.drawable.prepend_icon_products)

        subscribeEvents()

        when(intent.action) {
            ACTION_LOAD_BY_JOB_ORDER_ID -> {
                intent.getStringExtra(JOB_ORDER_ID).toUUID()?.let {
                    viewModel.loadByJobOrderId(it)
                }
            }
            ACTION_LOAD_BY_CUSTOMER_ID -> {
                intent.getStringExtra(CUSTOMER_ID).toUUID()?.let {
                    viewModel.loadByCustomer(it)
                }
            }
            ACTION_LOAD_EMPTY_JOB_ORDER -> {
                val customerId = intent.getStringExtra(CUSTOMER_ID).toUUID()
                viewModel.loadEmptyJobOrder(customerId)
            }
        }
    }

    private fun subscribeEvents() {
        binding.buttonPrint.setOnClickListener {
            viewModel.openPrinterOptions()
        }
//        binding.buttonPackages.setOnClickListener {
//            viewModel.openPackages()
//        }
//        pictureListAdapter.onItemClick = {
//            viewModel.openPictures()
//        }

        launcher.onOk = { result ->
            val data = result.data
            when(data?.action) {
                ACTION_SYNC_PACKAGE -> {
//                    data.getParcelableArrayListExtra<MenuServiceItem>(JobOrderCreateSelectPackageActivity.SERVICES)?.toList().let {
//                        viewModel.syncServices(it)
//                    }
//                    data.getParcelableArrayListExtra<MenuExtrasItem>(JobOrderCreateSelectPackageActivity.EXTRAS)?.toList().let {
//                        viewModel.syncExtras(it)
//                    }
//                    data.getParcelableArrayListExtra<MenuProductItem>(JobOrderCreateSelectPackageActivity.PRODUCTS)?.toList().let {
//                        viewModel.syncProducts(it)
//                    }
                    data.getParcelableArrayListExtra<MenuJobOrderPackage>(JobOrderCreateSelectPackageActivity.PACKAGES)?.toList().let {
                        viewModel.syncPackages(it)
                    }
                }
                ACTION_SELECT_CUSTOMER -> {
                    data.getStringExtra(CUSTOMER_ID).toUUID()?.let {
                        viewModel.setCustomerId(it)
                    }
                }
                ACTION_LOAD_BY_JOB_ORDER_ID -> {
                    data.getStringExtra(JOB_ORDER_ID).toUUID()?.let {
                        viewModel.loadByJobOrderId(it)
                    }
                }
                JobOrderPaymentActivity.ACTION_LOAD_BY_CUSTOMER -> {
                    data.getStringExtra(PAYMENT_ID).toUUID()?.let {
                        viewModel.loadPayment(it)
                    }
                }
                ACTION_SYNC_SERVICES -> {
                    data.getParcelableArrayListExtra<MenuServiceItem>(PAYLOAD_EXTRA)?.toList().let {
                        viewModel.syncServices(it)
                    }
                }
                ACTION_SYNC_PRODUCTS -> {
                    data.getParcelableArrayListExtra<MenuProductItem>(PAYLOAD_EXTRA)?.toList().let {
                        viewModel.syncProducts(it)
                    }
                }
                ACTION_SYNC_EXTRAS -> {
                    data.getParcelableArrayListExtra<MenuExtrasItem>(PAYLOAD_EXTRA)?.toList().let {
                        viewModel.syncExtras(it)
                    }
                }
                ACTION_SYNC_DELIVERY -> {
                    data.getParcelableExtra<DeliveryCharge>(PAYLOAD_EXTRA).let {
                        viewModel.setDeliveryCharge(it)
                    }
                }
                ACTION_SYNC_DISCOUNT -> {
                    data.getParcelableExtra<MenuDiscount>(PAYLOAD_EXTRA).let {
                        viewModel.applyDiscount(it)
                    }
                }
//                ACTION_SYNC_PAYMENT -> {
//                    viewModel.loadPayment()
//                }
                ACTION_DELETE_JOB_ORDER -> {
                    finish()
                }
//                ACTION_CONFIRM_SAVE -> {
//                    data.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
//                        viewModel.save(it.userId)
//                    }
//                }
//                ACTION_REQUEST_UNLOCK -> {
//                    viewModel.unlock()
//                }
            }
        }

        packageAdapter.onItemClick = {
            viewModel.openPackages(it)
//            JobOrderPackagePreviewBottomSheetFragment.newInstance(it.packageRefId).show(supportFragmentManager, null)
        }

        servicesAdapter.apply {
            onItemClick = {
                viewModel.openServices(it)
            }
            onDeleteRequest = {
                showDeleteConfirmationDialog("Remove this item?", "Are you sure you want to remove this item from the list?") {
                    viewModel.removeService(it.serviceRefId)
                }
            }
        }

        productsAdapter.apply {
            onItemClick = {
                viewModel.openProducts(it)
            }
            onDeleteRequest = {
                showDeleteConfirmationDialog("Remove this item?", "Are you sure you want to remove this item from the list?") {
                    viewModel.removeProduct(it.productRefId)
                }
            }
        }

        extrasAdapter.apply {
            onItemClick = {
                viewModel.openExtras(it)
            }
            onDeleteRequest = {
                showDeleteConfirmationDialog("Remove this item?", "Are you sure you want to remove this item from the list?") {
                    viewModel.removeExtras(it.extrasRefId)
                }
            }
        }

        binding.inclPackageLegend.cardLegend.setOnClickListener {
            viewModel.openPackages(null)
        }

        binding.inclServicesLegend.cardLegend.setOnClickListener {
            viewModel.openServices(null)
        }

        binding.inclProductsLegend.cardLegend.setOnClickListener {
            viewModel.openProducts(null)
        }

        binding.inclExtrasLegend.cardLegend.setOnClickListener {
            viewModel.openExtras(null)
        }

        viewModel.jobOrderPackages.observe(this, Observer {
            packageAdapter.setData(it.filter { !it.deleted })
        })

        viewModel.jobOrderServices.observe(this, Observer {
            servicesAdapter.setData(it.toMutableList())
        })

        viewModel.jobOrderProducts.observe(this, Observer {
            productsAdapter.setData(it.toMutableList())
        })

        viewModel.jobOrderExtras.observe(this, Observer {
            extrasAdapter.setData(it.toMutableList())
        })

        viewModel.unpaidJobOrders.observe(this, Observer {
            unpaidJobOrdersAdapter.setData(it)
        })

        viewModel.jobOrderPictures.observe(this, Observer {
            pictureListAdapter.setData(it)
        })

        binding.buttonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.buttonPayment.setOnClickListener {
            viewModel.openPayment()
        }
        binding.buttonDelete.setOnClickListener {
            viewModel.requestCancel()
        }
        binding.jobOrderGallery.setOnClickListener {
            viewModel.openPictures()
        }

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is CreateJobOrderViewModel.DataState.OpenPackages -> {
                    openPackages(it.list, it.item)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenServices -> {
                    openServices(it.list, it.item)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenProducts -> {
                    openProducts(it.list, it.item)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenExtras -> {
                    openExtras(it.list, it.item)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenDelivery -> {
                    openDelivery(it.deliveryCharge)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenDiscount -> {
                    openDiscount(it.discount)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenPictures -> {
                    JobOrderGalleryBottomSheetFragment.newInstance(it.jobOrderId).show(supportFragmentManager, null)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.ValidationPassed -> {
                    prepareSubmit()
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.InvalidOperation -> {
                    showDialog(it.message)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.SaveSuccess -> {
                    showDialog("Job Order saved successfully!")
                    viewModel.resetState()
                    startSync(it.jobOrderId)
                }
                is CreateJobOrderViewModel.DataState.OpenPayment -> {
                    openPayment(it.paymentId, it.customerId)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.MakePayment -> {
                    makePayment(it.customerId)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.RequestCancel -> {
                    val intent = Intent(this, JobOrderCancelActivity::class.java).apply {
                        action = ACTION_DELETE_JOB_ORDER
                        putExtra(JOB_ORDER_ID, it.jobOrderId.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.RequestExit -> {
                    confirmExit(it.canExit, it.resultCode, it.jobOrderId)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.PickCustomer -> {
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.SearchCustomer -> {
                    val intent = Intent(this, JobOrderCreateSelectCustomerActivity::class.java).apply {
                        action = ACTION_SELECT_CUSTOMER
                        putExtra(CUSTOMER_ID, it.customerId.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenPrinter -> {
                    openPrinterOptions(it.jobOrderId)
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        authViewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.Submit -> {
                    when(val authResult = it.data) {
                        is AuthResult.Authenticated -> {
                            permitted(authResult.loginCredentials, authResult.action)
                        }

                        is AuthResult.MandateAuthentication -> {
                            authLauncher.launch(authResult.permissions, authResult.action, true)
                        }

                        is AuthResult.OperationNotPermitted -> {
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })

    }

    private fun prepareSubmit() {
        authViewModel.authenticate(listOf(), ACTION_CONFIRM_SAVE, false)
//        authLauncher.launch(listOf(), ACTION_CONFIRM_SAVE, false)
//        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//            action = ACTION_CONFIRM_SAVE
//            putExtra(AuthActionDialogActivity.ACTION_EXTRA, "Confirm and save Job Order")
//        }
//        launcher.launch(intent)
    }

    private fun openPackages(packages: List<MenuJobOrderPackage>?, itemPreset: MenuJobOrderPackage?) {
        val intent = Intent(this, JobOrderCreateSelectPackageActivity::class.java).apply {
            action = ACTION_SYNC_PACKAGE
            packages?.let {
                putParcelableArrayListExtra(PAYLOAD_EXTRA, ArrayList(it))
            }
            putExtra(ITEM_PRESET_EXTRA, itemPreset)
        }
        launcher.launch(intent)
    }

    private fun openServices(services: List<MenuServiceItem>?, itemPreset: MenuServiceItem?) {
        val intent = Intent(this, JobOrderCreateSelectWashDryActivity::class.java).apply {
            action = ACTION_SYNC_SERVICES
            services?.let {
                putParcelableArrayListExtra(PAYLOAD_EXTRA, ArrayList(it))
                putExtra(ITEM_PRESET_EXTRA, itemPreset)
            }
        }
        launcher.launch(intent)
    }

    private fun openProducts(products: List<MenuProductItem>?, itemPreset: MenuProductItem?) {
        val intent = Intent(this, JobOrderCreateSelectProductsActivity::class.java).apply {
            action = ACTION_SYNC_PRODUCTS
            products?.let {
                putParcelableArrayListExtra(PAYLOAD_EXTRA, ArrayList(it))
                putExtra(ITEM_PRESET_EXTRA, itemPreset)
            }
        }
        launcher.launch(intent)
    }

    private fun openExtras(extras: List<MenuExtrasItem>?, itemPreset: MenuExtrasItem?) {
        val intent = Intent(this, JobOrderCreateSelectExtrasActivity::class.java).apply {
            action = ACTION_SYNC_EXTRAS
            extras?.let {
                putParcelableArrayListExtra(PAYLOAD_EXTRA, ArrayList(it))
                putExtra(ITEM_PRESET_EXTRA, itemPreset)
            }
        }
        launcher.launch(intent)
    }

    private fun openDelivery(deliveryCharge: DeliveryCharge?) {
        val intent = Intent(this, JobOrderCreateSelectDeliveryActivity::class.java).apply {
            action = ACTION_SYNC_DELIVERY
            deliveryCharge?.let {
                putExtra(PAYLOAD_EXTRA, deliveryCharge)
            }
        }
        launcher.launch(intent)
    }

    private fun openDiscount(discount: MenuDiscount?) {
        val intent = Intent(this, JobOrderCreateSelectDiscountActivity::class.java).apply {
            action = ACTION_SYNC_DISCOUNT
            discount?.let {
                putExtra(PAYLOAD_EXTRA, discount)
            }
        }
        launcher.launch(intent)
    }

    private fun makePayment(customerId: UUID) {
        val intent = Intent(this, JobOrderPaymentActivity::class.java).apply {
            action = JobOrderPaymentActivity.ACTION_LOAD_BY_CUSTOMER
            putExtra(CUSTOMER_ID, customerId.toString())
        }

        launcher.launch(intent)
    }

    private fun openPayment(paymentId: UUID?, customerId: UUID?) {
        val intent = Intent(this, PaymentPreviewActivity::class.java).apply {
            putExtra(PAYMENT_ID, paymentId.toString())
            putExtra(CUSTOMER_ID, customerId.toString())
        }

        launcher.launch(intent)
    }

    private fun openPrinterOptions(jobOrderId: UUID) {
        val intent = Intent(this, JobOrderPrintActivity::class.java).apply {
            putExtra(ID, jobOrderId.toString())
        }
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        viewModel.requestExit()
    }

    private fun confirmExit(promptPass: Boolean, resultCode: Int, jobOrderId: UUID?) {
        if((backPressed && !promptPass) || promptPass) {
            setResult(resultCode, Intent(intent.action).apply {
                putExtra(JOB_ORDER_ID, jobOrderId.toString())
            })
            finish()
        } else {
            Toast.makeText(this, "Press back again to revert changes", Toast.LENGTH_LONG).show()
            backPressed = true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                backPressed = false
            }, 2000)
        }
    }

    private fun startSync(jobOrderId: UUID) {
        if(internetAvailable) {
            JobOrderSyncService.start(this, jobOrderId)
        }
        ShopSetupSyncWorker.enqueue(this)
    }

    override fun onPause() {
        super.onPause()
        InternetConnectionObserver.unRegister()
    }

    override fun onConnected() {
        internetAvailable = true
        println("internet available")
    }

    override fun onDisconnected() {
        internetAvailable = false
        println("no internet available")
    }
    override fun onResume() {
        super.onResume()

        InternetConnectionObserver
            .instance(this)
            .setCallback(this)
            .register()
    }
}