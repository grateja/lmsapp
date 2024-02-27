package com.vag.lmsapp.app.discounts.edit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityDiscountAddEditBinding
import com.vag.lmsapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DiscountAddEditActivity(
//    override var requireAuthOnSave: Boolean = false,
//    override var requireAuthOnDelete: Boolean = false
) : CrudActivity() {
    private lateinit var binding: ActivityDiscountAddEditBinding
    private val viewModel: DiscountAddEditViewModel by viewModels()
    private val discountApplicableAdapter = Adapter<DiscountApplicableViewModel>(R.layout.recycler_item_discount_applicable)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discount_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerDiscountApplicable.adapter = discountApplicableAdapter

        subscribeListeners()
        subscribeEvents()
    }

    private fun subscribeEvents() {
        discountApplicableAdapter.onItemClick = {
            viewModel.syncApplicable(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    confirmExit(it.data.id)
                }
                is DataState.DeleteSuccess -> {
                    confirmExit(it.data.id)
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        viewModel.applicableTo.observe(this, Observer {
            discountApplicableAdapter.setData(it)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
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