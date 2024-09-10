package com.vag.lmsapp.app.packages.preview

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.joborders.create.extras.AvailableExtrasAdapter
import com.vag.lmsapp.app.joborders.create.products.AvailableProductsAdapter
import com.vag.lmsapp.app.joborders.create.services.AvailableServicesAdapter
import com.vag.lmsapp.app.packages.EnumPackageItemType
import com.vag.lmsapp.app.packages.edit.PackagesAddEditBottomSheetFragment
import com.vag.lmsapp.app.packages.list.PackageItem
import com.vag.lmsapp.databinding.ActivityPackagesPreviewBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.PACKAGE_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackagesPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPackagesPreviewBinding
    private val viewModel: PackagesPreviewViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val servicesAdapter = AvailableServicesAdapter()
    private val productsAdapter = AvailableProductsAdapter()
    private val extrasAdapter = AvailableExtrasAdapter()

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }
    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_DELETE_PACKAGE -> {
                viewModel.initiateDelete()
                finish()
            }
        }
    }

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
        binding.appbar.setOnClickListener {
            viewModel.openEdit()
        }

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

        binding.cardButtonConfirm.setOnClickListener {
            viewModel.save()
            finish()
        }

        binding.cardButtonClose.setOnClickListener {
            finish()
        }

        binding.buttonCardHideToggle.setOnClickListener {
            viewModel.hideToggle()
        }

        binding.cardButtonDelete.setOnClickListener {
            showDeleteConfirmationDialog("Delete package?", "Are you sure you want to delete this package?") {
                authViewModel.authenticate(listOf(EnumActionPermission.MODIFY_SERVICES_PACKAGES), ACTION_DELETE_PACKAGE, false)
//                authLauncher.launch(listOf(EnumActionPermission.MODIFY_SERVICES_PACKAGES), "Delete package", false)
            }
        }

        binding.cardButtonReset.setOnClickListener {
            viewModel.reset()
        }

//        authLauncher.onOk = { _, code ->
//            viewModel.initiateDelete()
//            finish()
//        }
    }

    private fun subscribeListeners() {
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
                                .setAnchorView(binding.controls)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                        // is AuthResult.NoAuthentication -> {
                        //     setupUi()
                        //  }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is PackagesPreviewViewModel.NavigationState.OpenEdit -> {
                    PackagesAddEditBottomSheetFragment.newInstance(it.packageId).show(supportFragmentManager, null)
                    viewModel.resetState()
                }
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

    companion object {
        private const val ACTION_DELETE_PACKAGE = "Delete package"
    }
}