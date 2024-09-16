package com.vag.lmsapp.app.packages.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.packages.preview.PackagesPreviewActivity
import com.vag.lmsapp.databinding.ActivityPackagesAddEditBinding
import com.vag.lmsapp.util.Constants.Companion.PACKAGE_ID
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PackagesAddEditActivity(
//    override var requireAuthOnSave: Boolean = false,
//    override var requireAuthOnDelete: Boolean = false
) : CrudActivity() {
    private lateinit var binding: ActivityPackagesAddEditBinding
    private val viewModel: PackagesAddEditViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.controls.viewModel = viewModel

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {

    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.DeleteSuccess -> {
                    finish()
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    val intent = Intent(this, PackagesPreviewActivity::class.java).apply {
                        putExtra(PACKAGE_ID, it.data.id.toString())
                    }
                    startActivity(intent)
                    finish()
                }

                else -> {}
            }
        })
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
//        viewModel.save()
//    }
//
//    override fun confirmDelete(loginCredentials: LoginCredentials?) {
//        viewModel.confirmDelete(loginCredentials?.userId)
//    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        viewModel.requestExit()
    }
}