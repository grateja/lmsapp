package com.vag.lmsapp.app.app_settings.text_message_templates.add_edit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityTextMessageTemplateAddEditBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class TextMessageTemplateAddEditActivity : CrudActivity() {
    private lateinit var binding: ActivityTextMessageTemplateAddEditBinding
    private val viewModel: TextMessageTemplateAddEditViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_message_template_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.controls.viewModel = viewModel
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        subscribeListeners()
        subscribeEvents()

        viewModel.get(intent.getStringExtra(ENTITY_ID).toUUID())
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }

                is DataState.SaveSuccess -> {
                    finish()
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
    }

    private fun subscribeEvents() {
        binding.controls.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun get(id: UUID?) {
        viewModel.get(id)
    }

//    override fun confirmDelete(loginCredentials: LoginCredentials?) {
//        viewModel.confirmDelete(loginCredentials?.userId)
//    }
//
//    override fun confirmSave(loginCredentials: LoginCredentials?) {
//        viewModel.save()
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

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }
}