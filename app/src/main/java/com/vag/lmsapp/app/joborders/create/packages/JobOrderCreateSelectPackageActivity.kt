package com.vag.lmsapp.app.joborders.create.packages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.shared_ui.CreateJobOrderModifyQuantityBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectPackageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectPackageActivity : AppCompatActivity() {
    companion object {
        const val SERVICES = "services"
        const val PRODUCTS = "products"
        const val EXTRAS = "extras"
        const val PACKAGES = "packages"
    }

    private lateinit var binding: ActivityJobOrderCreateSelectPackageBinding

    private val viewModel: AvailablePackageViewModel by viewModels()

    private lateinit var modifyQuantityDialog: CreateJobOrderModifyQuantityBottomSheetFragment

    private val adapter = Adapter<MenuJobOrderPackage>(R.layout.recycler_item_create_job_order_available_package)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_package)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerAvailablePackages.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        adapter.onItemClick = { itemClick(it) }
        binding.buttonOk.setOnClickListener {
            viewModel.prepareSubmit()
        }
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun subscribeListeners() {
        viewModel.availablePackages.observe(this, Observer {
            adapter.setData(it)
            intent.getParcelableArrayListExtra<MenuJobOrderPackage>(JobOrderCreateActivity.PAYLOAD_EXTRA).let {
                viewModel.setPreselectedPackages(it)
            }
//            viewModel.setPreselectedPackages(
//                intent.getParcelableArrayListExtra<MenuJobOrderPackage>("packages")?.toList()
//            )
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is AvailablePackageViewModel.DataState.UpdatePackage -> {
                    adapter.updateItem(it.packageItem)
                    viewModel.resetState()
                }
                is AvailablePackageViewModel.DataState.Submit -> {
//                    println("services size")
//                    println(it.services?.size)
                    submit(it)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun itemClick(item: MenuJobOrderPackage) {
        requestModifyQuantity(
            QuantityModel(item.packageRefId, item.packageName, item.quantity, QuantityModel.TYPE_PACKAGE)
        )
    }

    private fun requestModifyQuantity(quantityModel: QuantityModel) {
        CreateJobOrderModifyQuantityBottomSheetFragment.newInstance(quantityModel, QuantityModel.TYPE_PACKAGE).apply {
            onOk = {
                viewModel.putPackage(it)
            }
            onItemRemove = {
                viewModel.removePackage(it)
            }
        }.show(supportFragmentManager, this.toString())
    }

    private fun submit(data: AvailablePackageViewModel.DataState.Submit) {
        setResult(RESULT_OK, Intent().apply {
            action = intent.action
//            putParcelableArrayListExtra(SERVICES, data.services?.let { ArrayList(it) })
//            putParcelableArrayListExtra(EXTRAS, data.extras?.let { ArrayList(it) })
//            putParcelableArrayListExtra(PRODUCTS, data.products?.let { ArrayList(it) })
            putParcelableArrayListExtra(PACKAGES, data.packages?.let { ArrayList(it) })
        })
        finish()
    }
}