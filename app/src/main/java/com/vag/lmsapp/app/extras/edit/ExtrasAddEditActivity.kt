package com.vag.lmsapp.app.extras.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityExtrasAddEditBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.selectAllOnFocus
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExtrasAddEditActivity : CrudActivity() {
    private lateinit var binding: ActivityExtrasAddEditBinding
    private val viewModel: ExtrasAddEditViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_extras_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.controls.viewModel = viewModel

        binding.textPrice.selectAllOnFocus()

        subscribeListeners()
    }
    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    ShopSetupSyncWorker.enqueue(this)
                    confirmExit(it.data.id)
                }
                is DataState.DeleteSuccess -> {
                    ShopSetupSyncWorker.enqueue(this)
                    confirmExit(it.data.id)
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
        viewModel.categories.observe(this, Observer {
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, it)
            binding.textCategory.setAdapter(adapter)
        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        viewModel.requestExit()
    }

    override fun get(id: UUID?) {
        viewModel.get(id)
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun confirmSave(loginCredentials: LoginCredentials?) {
        viewModel.save()
    }


    override fun confirmDelete(loginCredentials: LoginCredentials?) {
        viewModel.confirmDelete(loginCredentials?.userId)
    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }
}