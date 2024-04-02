package com.vag.lmsapp.app.app_settings.user.preview

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.user.add_edit.UserAccountAddEditBottomSheetFragment
import com.vag.lmsapp.app.app_settings.user.change_password.AppSettingsChangePasswordActivity
import com.vag.lmsapp.databinding.ActivityUserAccountPreviewBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class UserAccountPreviewActivity : AppCompatActivity() {
    private val viewModel: UserAccountPreviewViewModel by viewModels()
    private lateinit var binding: ActivityUserAccountPreviewBinding
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)
    private var authId: UUID? = null
    private var userId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerViewPermissions.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        userId = intent.getStringExtra(USER_ID).toUUID()?.also {
            viewModel.setUserId(it)
        }
        authId = intent.getStringExtra(AUTH_ID).toUUID()?.also {
            viewModel.setAuth(it)
        }
    }

    private fun subscribeEvents() {
        binding.cardChangePassword.setOnClickListener {
            val intent = Intent(this, AppSettingsChangePasswordActivity::class.java).apply {
                putExtra(AUTH_ID, authId.toString())
            }
            startActivity(intent)
        }
        binding.cardEditAccountInfo.setOnClickListener {
            UserAccountAddEditBottomSheetFragment.newInstance(userId!!).show(supportFragmentManager, null)
        }
    }

    private fun subscribeListeners() {
        viewModel.user.observe(this, Observer {
            adapter.setData(it.user.permissions.map {it.toString()})
        })
    }
}