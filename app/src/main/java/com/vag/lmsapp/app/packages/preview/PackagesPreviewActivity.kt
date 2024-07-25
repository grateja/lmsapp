package com.vag.lmsapp.app.packages.preview

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.extras.AvailableExtrasAdapter
import com.vag.lmsapp.app.joborders.create.products.AvailableProductsAdapter
import com.vag.lmsapp.app.joborders.create.services.AvailableServicesAdapter
import com.vag.lmsapp.app.packages.EnumPackageItemType
import com.vag.lmsapp.app.packages.list.PackageItem
import com.vag.lmsapp.databinding.ActivityPackagesPreviewBinding
import com.vag.lmsapp.util.Constants.Companion.PACKAGE_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackagesPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPackagesPreviewBinding
    private val viewModel: PackagesPreviewViewModel by viewModels()

    private val servicesAdapter = AvailableServicesAdapter()
    private val productsAdapter = AvailableProductsAdapter()
    private val extrasAdapter = AvailableExtrasAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerViewServices.adapter = servicesAdapter
        binding.recyclerViewProducts.adapter = productsAdapter
        binding.recyclerViewExtras.adapter = extrasAdapter

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(PACKAGE_ID).toUUID().let {
            viewModel.get(it)
        }
    }

    private fun subscribeEvents() {
        servicesAdapter.onItemClick = {
            viewModel.selectPackageItem(
                PackageItem(
                EnumPackageItemType.WASH_DRY,
                it.serviceRefId,
                it.abbr(),
                it.description(),
                it.price,
                it.quantity,
                !it.selected
            )
            )
        }

        productsAdapter.onItemClick = {
            viewModel.selectPackageItem(
                PackageItem(
                EnumPackageItemType.PRODUCTS,
                it.productRefId,
                it.name,
                it.serving(),
                it.price,
                it.quantity,
                !it.selected
            )
            )
        }

        extrasAdapter.onItemClick = {
            viewModel.selectPackageItem(
                PackageItem(
                EnumPackageItemType.EXTRAS,
                it.extrasRefId,
                it.name,
                it.category ?: "",
                it.price,
                it.quantity,
                !it.selected
            )
            )
        }

        binding.buttonCardConfirm.setOnClickListener {
            viewModel.save()
            finish()
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is PackagesPreviewViewModel.NavigationState.SelectPackageItem -> {
                    PackagePreviewSelectItemBottomSheetFragment.newInstance(it.packageItem).apply {
                        onOk = {
                            viewModel.updateItem(it)
                        }
                    }
                        .show(supportFragmentManager, null)
                    viewModel.resetState()
                }

                is PackagesPreviewViewModel.NavigationState.UpdateAvailableServices -> {
                    servicesAdapter.updateItem(it.menuServiceItem)
                    viewModel.resetState()
                }
                is PackagesPreviewViewModel.NavigationState.UpdateAvailableProducts -> {
                    productsAdapter.updateItem(it.menuProductItem)
                    viewModel.resetState()
                }
                is PackagesPreviewViewModel.NavigationState.UpdateAvailableExtras -> {
                    extrasAdapter.updateItem(it.menuExtrasItem)
                    viewModel.resetState()
                }
                else -> {}
            }
        })

//        viewModel.packageServices.observe(this, Observer {
//            viewModel.preselectServices(it)
//        })
//        viewModel.packageProducts.observe(this, Observer {
//            viewModel.preselectProducts(it)
//        })
//        viewModel.packageExtras.observe(this, Observer {
//            viewModel.preselectExtras(it)
//        })

        viewModel.availableServices.observe(this, Observer {
            it?.let {
                servicesAdapter.setData(it)
            }
        })
        viewModel.availableProducts.observe(this, Observer {
            it?.let {
                productsAdapter.setData(it)
            }
        })
        viewModel.availableExtras.observe(this, Observer {
            it?.let {
                extrasAdapter.setData(it)
            }
        })
    }
}