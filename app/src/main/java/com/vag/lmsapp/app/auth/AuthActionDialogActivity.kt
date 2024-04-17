package com.vag.lmsapp.app.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.databinding.ActivityAuthActionDialogBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumAuthMethod
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDialog
import com.itsxtt.patternlock.PatternLockView
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.FragmentsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActionDialogActivity : AppCompatActivity() {
    companion object {
        const val AUTH_ACTION = "authAction"

        const val MESSAGE = "message"
        const val PERMISSIONS_EXTRA = "permissions"
        const val ROLES_EXTRA = "roles"
        const val LAUNCH_CODE = "launchCode"

        @SuppressLint("Returns Login Credentials if Authentication succeeded")
        const val RESULT = "LoginCredential"
//
//        fun launch(context: Context, launcher: ActivityLauncher, permissions: List<EnumActionPermission>, onOk: ((ActivityResult) -> Unit) ?) {
//            launcher.onOk = {
//                onOk?.invoke(it)
//            }
//            val intent = Intent(context, AuthActionDialogActivity::class.java).apply {
//                putExtra(MESSAGE, "Authentication Required")
//                putExtra(PERMISSIONS_EXTRA, ArrayList(permissions))
//            }
//            launcher.launch(intent)
//        }
//
//        fun launch(context: Context, launcher: FragmentLauncher, permissions: List<EnumActionPermission>, onOk: ((ActivityResult) -> Unit) ?) {
//            launcher.onOk = {
//                onOk?.invoke(it)
//            }
//            val intent = Intent(context, AuthActionDialogActivity::class.java).apply {
//                putExtra(MESSAGE, "Authentication Required")
//                putExtra(PERMISSIONS_EXTRA, ArrayList(permissions))
//            }
//            launcher.launch(intent)
//        }
    }

    private lateinit var binding: ActivityAuthActionDialogBinding
    private val viewModel: AuthDialogViewModel by viewModels()
    private val privilegeAdapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_action_dialog)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()
    }

    override fun onResume() {
        super.onResume()

        intent.getParcelableArrayListExtra<EnumActionPermission>(PERMISSIONS_EXTRA)?.let {
            println("permissions")
            println(it)
            viewModel.setPermissions(it)
        }
        intent.getParcelableArrayListExtra<Role>(ROLES_EXTRA)?.let {
            println("roles")
            println(it)
            viewModel.setRoles(it)
        }
        intent.getStringExtra(MESSAGE)?.let {
            viewModel.setMessage(it)
        }
    }

    private fun subscribeEvents() {
        binding.textInputCashlessProvider.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.post {
                    binding.textInputCashlessProvider.selectAll()
                }
            }
        }
        binding.buttonOk.setOnClickListener {
            viewModel.validate(AuthDialogViewModel.AuthMethod.AuthByPassword)
        }
        binding.buttonCancel.setOnClickListener {
            finish()
        }
        binding.buttonAuthMethodPassword.setOnClickListener {
            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_PASSWORD)
        }
        binding.buttonAuthMethodPattern.setOnClickListener {
            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_PATTERN)
        }
//        binding.buttonAuthMethodBiometric.setOnClickListener {
//            viewModel.setAuthMethod(EnumAuthMethod.AUTH_BY_BIOMETRIC)
//        }
        binding.buttonPrivilege.setOnClickListener {
            val requiredAuthPrivilegesFragment = RequiredAuthPrivilegesFragment.newInstance()
            requiredAuthPrivilegesFragment.show(supportFragmentManager, "privilege")
        }
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
                        putExtra(LAUNCH_CODE, intent.getIntExtra(LAUNCH_CODE, -1))
                        putExtra(RESULT, it.data)
                    })
                    finish()
                }
                is DataState.Invalidate -> {
                    showDialog(it.message)
                }

                else -> {}
            }
        })
    }
}