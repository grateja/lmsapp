package com.vag.lmsapp.app.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.databinding.ActivityAuthActionDialogBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.util.DataState
import com.itsxtt.patternlock.PatternLockView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActionDialogActivity : AppCompatActivity() {
    companion object {
//        const val ACTION_EXTRA = "action_extra"
        const val PERMISSIONS_EXTRA = "permissions"
        const val ROLES_EXTRA = "roles"
        const val LAUNCH_CODE = "launchCode"
        const val MANDATE = "pre_login"

        @SuppressLint("Returns Login Credentials if Authentication succeeded")
        const val RESULT = "LoginCredential"
    }

    private lateinit var binding: ActivityAuthActionDialogBinding
    private val viewModel: AuthDialogViewModel by viewModels()
    private val privilegeAdapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_action_dialog)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
//        binding.recyclerViewPermissions.adapter = privilegeAdapter

        subscribeEvents()
        subscribeListeners()

        intent.getParcelableArrayListExtra<EnumActionPermission>(PERMISSIONS_EXTRA)?.let {
            viewModel.setPermissions(it)
        }

        intent.getParcelableArrayListExtra<Role>(ROLES_EXTRA)?.let {
            viewModel.setRoles(it)
        }

        intent.getStringExtra(LAUNCH_CODE)?.let {
            viewModel.setLaunchCode(it)
        }

//        val mandate = intent.getBooleanExtra(MANDATE, true)

//        viewModel.checkSecurityType(mandate)
    }

    private fun subscribeEvents() {
        binding.textInputCashlessProvider.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.post {
                    binding.textInputCashlessProvider.selectAll()
                    binding.textInputCashlessProvider.showDropDown()
                }
            }
        }
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate(AuthDialogViewModel.AuthMethod.AuthByPassword)
        }
        binding.cardButtonClose.setOnClickListener {
            close()
        }
//        binding.buttonAuthMethodPassword.setOnClickListener {
//            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_PASSWORD)
//        }
//        binding.buttonAuthMethodPattern.setOnClickListener {
//            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_PATTERN)
//        }
//        binding.buttonAuthMethodBiometric.setOnClickListener {
//            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_BIOMETRIC)
//        }
//        binding.buttonPrivilege.setOnClickListener {
//            val requiredAuthPrivilegesFragment = RequiredAuthPrivilegesFragment.newInstance()
//            requiredAuthPrivilegesFragment.show(supportFragmentManager, "privilege")
//        }
        binding.patternLock.setOnPatternListener(object : PatternLockView.OnPatternListener {
            override fun onComplete(ids: ArrayList<Int>): Boolean {
                viewModel.validate(AuthDialogViewModel.AuthMethod.AuthByPattern(ids))
                return true
            }
        })
    }

    private fun subscribeListeners() {
        viewModel.emails.observe(this, Observer {
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, it)
            binding.textInputCashlessProvider.setAdapter(adapter)
        })
        viewModel.permissions.observe(this, Observer {
            privilegeAdapter.setData(it.map {
                it.description
            })
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        action = intent.action
                        putExtra(LAUNCH_CODE, intent.getStringExtra(LAUNCH_CODE))
                        putExtra(RESULT, it.data)
                    })
                    finish()
                    viewModel.clearState()
                }
                is DataState.Invalidate -> {
                    binding.container.post {
                        Snackbar.make(binding.container, it.message, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        close()
    }

    private fun close() {
        setResult(RESULT_CANCELED, Intent().apply {
            action = intent.action
            putExtra(LAUNCH_CODE, intent.getStringExtra(LAUNCH_CODE))
        })
        finish()
    }
}