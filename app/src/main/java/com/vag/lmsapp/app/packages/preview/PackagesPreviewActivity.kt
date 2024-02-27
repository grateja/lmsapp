package com.vag.lmsapp.app.packages.preview

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.extras.JobOrderCreateSelectExtrasActivity
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.app.joborders.create.products.JobOrderCreateSelectProductsActivity
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.services.JobOrderCreateSelectWashDryActivity
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.app.packages.PackageItem
import com.vag.lmsapp.app.packages.edit.PackagesAddEditActivity
import com.vag.lmsapp.databinding.ActivityPackagesPreviewBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PackagesPreviewActivity : AppCompatActivity() {
    companion object {
        const val OPEN_ADD_EDIT_ACTION = "openAddEdit"
    }

    private lateinit var binding: ActivityPackagesPreviewBinding
    private val viewModel: PackagesPreviewViewModel by viewModels()

    private val launcher = ActivityLauncher(this)

    private val servicesAdapter = Adapter<PackageItem>(R.layout.recycler_item_package_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.serviceItems.adapter = servicesAdapter

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(CrudActivity.ENTITY_ID).toUUID().let {
            if(it == null) {
                viewModel.openAddEdit()
            } else {
                viewModel.get(it)
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//    }

    private fun subscribeEvents() {
        binding.buttonWashDry.setOnClickListener {
            viewModel.openWashDry()
        }

        binding.buttonProducts.setOnClickListener {
            viewModel.openProducts()
        }

        binding.buttonExtras.setOnClickListener {
            viewModel.openExtras()
        }

        binding.buttonEdit.setOnClickListener {
            viewModel.openAddEdit()
        }

        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Delete this item")
                setMessage("Are you sure you want to proceed?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.delete()
                    finish()
                    //confirmDelete(loginCredentials)
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                create()
            }.show()
        }

        launcher.onOk = {
            when(it.data?.action) {
                JobOrderCreateActivity.ACTION_SYNC_SERVICES -> {
                    val services = it.data?.getParcelableArrayListExtra<MenuServiceItem>(JobOrderCreateActivity.PAYLOAD_EXTRA)
                    viewModel.syncServices(services)
                }
                JobOrderCreateActivity.ACTION_SYNC_PRODUCTS -> {
                    val products = it.data?.getParcelableArrayListExtra<MenuProductItem>(JobOrderCreateActivity.PAYLOAD_EXTRA)
                    viewModel.syncProducts(products)
                }
                JobOrderCreateActivity.ACTION_SYNC_EXTRAS -> {
                    val extras = it.data?.getParcelableArrayListExtra<MenuExtrasItem>(JobOrderCreateActivity.PAYLOAD_EXTRA)
                    viewModel.syncExtras(extras)
                }
                OPEN_ADD_EDIT_ACTION -> {
                    val entityId = it.data?.getStringExtra(CrudActivity.ENTITY_ID).toUUID()
                    viewModel.get(entityId)
                }
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is PackagesPreviewViewModel.NavigationState.OpenAddEdit -> {
                    val intent = Intent(this, PackagesAddEditActivity::class.java).apply {
                        action = OPEN_ADD_EDIT_ACTION
                        putExtra(CrudActivity.ENTITY_ID, it.packageId.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is PackagesPreviewViewModel.NavigationState.OpenWashDry -> {
                    val intent = Intent(this, JobOrderCreateSelectWashDryActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_SYNC_SERVICES
                        it.items?.let { items ->
                            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(items))
                        }
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is PackagesPreviewViewModel.NavigationState.OpenProducts -> {
                    val intent = Intent(this, JobOrderCreateSelectProductsActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_SYNC_PRODUCTS
                        it.items?.let { items ->
                            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(items))
                        }
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is PackagesPreviewViewModel.NavigationState.OpenExtras -> {
                    val intent = Intent(this, JobOrderCreateSelectExtrasActivity::class.java).apply {
                        action = JobOrderCreateActivity.ACTION_SYNC_EXTRAS
                        it.items?.let { items ->
                            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(items))
                        }
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        viewModel.readOnlyModel.observe(this, Observer {
            it?.simpleList()?.let {
                servicesAdapter.setData(it)
            }
        })

//        viewModel.items.observe(this, Observer {
//            servicesAdapter.setData(it)
//        })
    }
}