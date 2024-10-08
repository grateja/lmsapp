package com.vag.lmsapp.app.products.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityProductAddEditBinding
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.util.*
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProductAddEditActivity(
//    override var requireAuthOnSave: Boolean = false,
//    override var requireAuthOnDelete: Boolean = false
) : CrudActivity() {
    private lateinit var binding: ActivityProductAddEditBinding
    private val viewModel: ProductAddEditViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_SAVE -> {
                viewModel.save(loginCredentials.userId)
            }

            ACTION_DELETE -> {
                viewModel.confirmDelete(loginCredentials.userId)
            }
        }
    }
    //    private val authLauncher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.spinnerMeasureUnit.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, EnumMeasureUnit.values())

        binding.controls.viewModel = viewModel

        binding.textCurrentStock.selectAllOnFocus()
        binding.textPrice.selectAllOnFocus()
        binding.textUnitPerServe.selectAllOnFocus()

        subscribeListeners()
        subscribeEvents()

//        intent.getStringExtra(ENTITY_ID).toUUID().let {
//            viewModel.get(it)
//        }
    }
    private fun subscribeEvents() {
//        binding.controls.buttonSave.setOnClickListener {
//            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//                action = ACTION_SAVE
//            }
//            authLauncher.launch(intent)
//        }
//        binding.controls.buttonDelete.setOnClickListener {
//            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//                action = ACTION_DELETE
//            }
//            authLauncher.launch(intent)
//        }
//        binding.controls.buttonCancel.setOnClickListener {
//            viewModel.requestExit()
//        }

//        authLauncher.onOk = {
//            val loginCredentials = it.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)
//            if(it.data?.action == ACTION_SAVE) {
//                viewModel.save()
//            } else if(it.data?.action == ACTION_DELETE) {
//                AlertDialog.Builder(this).apply {
//                    setTitle("Delete this item")
//                    setMessage("Are you sure you want to proceed?")
//                    setPositiveButton("Yes") { _, _ ->
//                        viewModel.confirmDelete(loginCredentials?.userId)
//                    }
//                    create()
//                }.show()
//            }
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
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    confirmExit(it.data.id)
                }
                is DataState.DeleteSuccess -> {
                    ShopSetupSyncWorker.enqueue(this)
                    viewModel.resetState()
                    confirmExit(it.data.id)
                }
                is DataState.RequestExit -> {
                    ShopSetupSyncWorker.enqueue(this)
                    viewModel.resetState()
                    confirmExit(it.promptPass)
                }
                is DataState.ValidationPassed -> {
//                    viewModel.save()
                    authViewModel.authenticate(listOf(), ACTION_SAVE, false)
//                    authenticate(ACTION_SAVE)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.requestExit()
    }

    override fun get(id: UUID?) {
        viewModel.get(id)
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun onDelete() {
        showDeleteConfirmationDialog {
            viewModel.confirmDelete(null)
//            authViewModel.authenticate(listOf(), ACTION_DELETE, false)
        }
    }
//    override fun confirmSave(loginCredentials: LoginCredentials?) {
//        loginCredentials?.userId?.let {
//            viewModel.save(it)
//        }
//    }
//
//    override fun confirmDelete(loginCredentials: LoginCredentials?) {
//        viewModel.confirmDelete(loginCredentials?.userId)
//    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }
}