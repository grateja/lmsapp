package com.vag.lmsapp.app.packages.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.packages.edit.PackagesAddEditBottomSheetFragment
import com.vag.lmsapp.app.packages.preview.PackagesPreviewActivity
import com.vag.lmsapp.databinding.ActivityPackagesBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.PACKAGE_ID
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PackagesActivity : FilterActivity() {
    override var filterHint = "Search package name"
    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    override var enableAdvancedFilter = false
    private lateinit var binding: ActivityPackagesBinding
    private val viewModel: PackagesViewModel by viewModels()
    private val adapter = PackagesAdapter()
    private val launcher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages)
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerPackages.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            openAddEdit(it.prePackage.id)
        }

        binding.buttonCreateNew.setOnClickListener {
            PackagesAddEditBottomSheetFragment().apply {
                onOk = {
                    openAddEdit(it?.id)
                }
            }.show(supportFragmentManager, "")
        }
    }

    private fun subscribeListeners() {
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    adapter.setData(it.items)
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    private fun openAddEdit(packageId: UUID?) {
        val intent = Intent(this, PackagesPreviewActivity::class.java).apply {
            putExtra(PACKAGE_ID, packageId.toString())
        }
        launcher.launch(intent)
    }
}