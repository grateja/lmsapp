package com.vag.lmsapp.app.app_settings.user.list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.user.preview.UserAccountPreviewActivity
import com.vag.lmsapp.app.app_settings.user.UserPreview
import com.vag.lmsapp.app.app_settings.user.add_edit.UserAccountAddEditBottomSheetFragment
import com.vag.lmsapp.app.app_settings.user.list.advanced_filter.UserAccountAdvancedFilter
import com.vag.lmsapp.app.app_settings.user.list.advanced_filter.UserAccountAdvancedFilterBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityAppSettingsUserAccountsBinding
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AppSettingsUserAccountsActivity : FilterActivity() {
    private lateinit var binding: ActivityAppSettingsUserAccountsBinding
    private val viewModel: AppSettingsUserAccountsViewModel by viewModels()
    private val adapter = Adapter<UserPreview>(R.layout.recycler_item_user_list_item)
    private var authId: UUID? = null
    override var filterHint = "Search staff name"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_user_accounts)
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerViewUsers.adapter = adapter

        authId = intent.getStringExtra(AUTH_ID).toUUID()

        subscribeEvents()
        subscribeListeners()

        viewModel.setFilterParams(UserAccountAdvancedFilter())
        viewModel.filter(true)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    override fun onAdvancedSearchClick() {
        viewModel.showAdvancedFilter()
    }

    private fun subscribeEvents() {
        binding.tabRoles.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> viewModel.setRole(null)
                    1 -> viewModel.setRole(Role.OWNER)
                    2 -> viewModel.setRole(Role.STAFF)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) { }

            override fun onTabReselected(p0: TabLayout.Tab?) { }
        })

        adapter.onItemClick = {
            openUserPreview(it.user.id)
        }

        binding.buttonCreateNew.setOnClickListener {
            add()
        }
    }

    private fun subscribeListeners() {
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    viewModel.clearState()
                }

                is FilterState.ShowAdvancedFilter -> {
                    UserAccountAdvancedFilterBottomSheetFragment.newInstance(it.advancedFilter).apply {
                        onOk = {
                            viewModel.setFilterParams(it)
                            viewModel.filter(true)
                        }
                    }.show(supportFragmentManager, null)
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    private fun openUserPreview(userId: UUID) {
        val intent = Intent(this, UserAccountPreviewActivity::class.java).apply {
            putExtra(USER_ID, userId.toString())
            putExtra(AUTH_ID, authId.toString())
        }
        startActivity(intent)
    }

    private fun add() {
        UserAccountAddEditBottomSheetFragment().show(supportFragmentManager, null)
    }
}