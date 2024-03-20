package com.vag.lmsapp.app.expenses.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityExpenseAddEditBinding
import com.vag.lmsapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExpenseAddEditActivity(
//    override var requireAuthOnSave: Boolean = true,
//    override var requireAuthOnDelete: Boolean = true
) : CrudActivity() {
    companion object {
        const val MODIFY_DATE_ACTION = "modifyDate"
    }

    private lateinit var binding: ActivityExpenseAddEditBinding
    private val viewModel: ExpenseAddEditViewModel by viewModels()
    private val dateTimePicker = DateTimePicker(this)
     private val launcher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeListeners()
        subscribeEvents()

        window.statusBarColor = getColor(R.color.color_code_expenses)
        binding.textAmount.selectAllOnFocus()
    }

    private fun subscribeEvents() {
        binding.textDatePaid.setOnClickListener {
            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
                action = MODIFY_DATE_ACTION
            }
            launcher.launch(intent)
        }

        launcher.onOk = {
            if(it.data?.action == MODIFY_DATE_ACTION) {
                dateTimePicker.show(viewModel.getDateCreated())
            }
        }

        dateTimePicker.onDateTimeSelected = {
            viewModel.setDateCreated(it)
        }

        binding.textTag.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.post {
                    binding.textTag.selectAll()
                }
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    authenticate(ACTION_SAVE)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    confirmExit(it.data.id)
                    viewModel.resetState()
                }
                is DataState.DeleteSuccess -> {
                    confirmExit(it.data.id)
                    viewModel.resetState()
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        viewModel.tags.observe(this, Observer {
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, it)
            binding.textTag.setAdapter(adapter)

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

    override fun confirmDelete(loginCredentials: LoginCredentials?) {
        viewModel.confirmDelete(loginCredentials?.userId)
    }

    override fun confirmSave(loginCredentials: LoginCredentials?) {
        viewModel.save(loginCredentials?.userId)
    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }
}