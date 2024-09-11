package com.vag.lmsapp.app.app_settings.user.preview

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.user.add_edit.UserAccountAddEditBottomSheetFragment
import com.vag.lmsapp.app.app_settings.user.change_password.AppSettingsChangePasswordActivity
import com.vag.lmsapp.app.app_settings.user.change_pattern.ChangePatternActivity
import com.vag.lmsapp.databinding.ActivityUserAccountPreviewBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class UserAccountPreviewActivity : AppCompatActivity() {
    private val viewModel: UserAccountPreviewViewModel by viewModels()
    private lateinit var binding: ActivityUserAccountPreviewBinding
    private val adapter = Adapter<String>(R.layout.recycler_item_simple_item)
//    private var authId: UUID? = null
//    private var userId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerViewPermissions.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(USER_ID).toUUID()?.also {
            viewModel.setUserId(it)
        }
        intent.getStringExtra(AUTH_ID).toUUID()?.also {
            viewModel.setAuth(it)
        }
    }

    private fun subscribeEvents() {
        binding.cardChangePassword.setOnClickListener {
            viewModel.openChangePassword()
        }
        binding.cardChangePattern.setOnClickListener {
            viewModel.openChangePattern()
        }
        binding.cardEditAccountInfo.setOnClickListener {
            viewModel.openEdit()
        }
        binding.cardEditDelete.setOnClickListener {
            showDeleteConfirmationDialog("Delete user account?", "Are you sure you want to delete this user account?\nThis action cannot be undone!") {
                viewModel.delete()
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.user.observe(this, Observer {
            it?.let {up ->
                adapter.setData(up.user.permissions.map {it.toString()})
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is UserAccountPreviewViewModel.NavigationState.DeleteSuccess -> {
                    finish()
                    viewModel.resetState()
                }

                is UserAccountPreviewViewModel.NavigationState.Invalidate -> {
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                is UserAccountPreviewViewModel.NavigationState.OpenEdit -> {
                    UserAccountAddEditBottomSheetFragment.newInstance(it.userId, it.authorizedUserId).show(supportFragmentManager, null)
                    viewModel.resetState()
                }

                is UserAccountPreviewViewModel.NavigationState.ChangePassword -> {
                    val intent = Intent(this, AppSettingsChangePasswordActivity::class.java).apply {
                        putExtra(USER_ID, it.userId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is UserAccountPreviewViewModel.NavigationState.ChangePattern -> {
                    val intent = Intent(this, ChangePatternActivity::class.java).apply {
                        putExtra(USER_ID, it.userId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}