package com.vag.lmsapp.app.products.add_stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.FragmentProductAddStockBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.services.InventoryLogSyncService
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.AuthLauncherFragment
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.Constants.Companion.PRODUCT_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.selectAllOnFocus
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ProductAddStockBottomSheetFragment : ModalFragment<UUID>() {
    override var fullHeight = true
    private lateinit var binding: FragmentProductAddStockBottomSheetBinding
    private val viewModel: ProductAddStockViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherFragment(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_ADD_STOCK -> {
                viewModel.save(loginCredentials.userId)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductAddStockBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.textQuantity.selectAllOnFocus()
        binding.textTotalAmount.selectAllOnFocus()

        subscribeEvents()
        subscribeListeners()

        val productId = arguments?.getString(PRODUCT_ID).toUUID()
        val inventoryLogId = arguments?.getString(ID).toUUID()

        viewModel.get(inventoryLogId, productId)

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.cardButtonClose.setOnClickListener {
            dismiss()
        }
        authLauncher.onOk = { loginCredentials, code ->
            viewModel.save(loginCredentials.userId)
        }
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
                                .setAnchorView(binding.cardButtonClose)
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

        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    authViewModel.authenticate(listOf(), ACTION_ADD_STOCK, false)
//                    authLauncher.launch(listOf(EnumActionPermission.MODIFY_INVENTORY), ACTION_ADD_STOCK, false)
                    viewModel.resetState()
                }

                is DataState.SaveSuccess -> {
                    viewModel.resetState()
                    dismiss()
                    InventoryLogSyncService.start(requireContext(), it.data.id)
                }

                else -> {}
            }
        })
    }

    companion object {
        private const val ACTION_ADD_STOCK = "Add stock"
        fun newInstance(productId: UUID, inventoryLogId: UUID?): ProductAddStockBottomSheetFragment {
            val args = Bundle().apply {
                putString(ID, inventoryLogId.toString())
                putString(PRODUCT_ID, productId.toString())
            }

            val fragment = ProductAddStockBottomSheetFragment().apply {
                arguments = args
            }
            return fragment
        }
    }
}