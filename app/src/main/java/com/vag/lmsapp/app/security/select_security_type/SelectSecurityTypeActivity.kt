package com.vag.lmsapp.app.security.select_security_type

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.databinding.ActivitySelectSecurityTypeBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.util.AuthLauncherActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectSecurityTypeActivity : AppCompatActivity() {
    companion object {
        private const val LAUNCH_CODE_SECURITY_NONE = "Select security code: None"
    }

    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item).apply {
        setData(listOf(
            "View daily sales",
            "Modify or delete job orders",
            "Modify pricing/services/products",
            "Update stocks manually",
            "Modify settings",
            "etc..."
        ))
    }

    private val viewModel: SelectSecurityTypeViewModel by viewModels()
    private lateinit var binding: ActivitySelectSecurityTypeBinding
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code ->
            if(code == LAUNCH_CODE_SECURITY_NONE) {
                viewModel.setSecurityType(EnumSecurityType.NONE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_security_type)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerViewSensitiveActions.adapter = adapter

        subscribeEvents()
    }

    private fun subscribeEvents() {
        binding.cardButtonClose.setOnClickListener {
            finish()
        }

        binding.cardButtonConfirm.setOnClickListener {
            viewModel.confirm()
            finish()
        }

        binding.cardSecurityTypeSensitiveActions.setOnClickListener {
            viewModel.setSecurityType(EnumSecurityType.SENSITIVE_ACTIONS)
        }

//        binding.cardSecurityTypeNone.setOnClickListener {
//            authLauncher.launch(listOf(EnumActionPermission.MODIFY_SETTINGS_SECURITY), LAUNCH_CODE_SECURITY_NONE, true)
//        }

        binding.cardSecurityTypeAppOpened.setOnClickListener {
            viewModel.setSecurityType(EnumSecurityType.START_UP)
        }
    }
}