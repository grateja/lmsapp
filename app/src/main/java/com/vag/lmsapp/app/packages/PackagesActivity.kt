package com.vag.lmsapp.app.packages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PackagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPackagesBinding
    private val viewModel: PackagesViewModel by viewModels()
    private val adapter = PackagesAdapter()
    private val launcher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerPackages.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            openAddEdit(it.prePackage.id)
        }

        binding.buttonCreateNew.setOnClickListener {
            PackagesAddEditBottomSheetFragment().show(supportFragmentManager, "")
        }
    }

    private fun subscribeListeners() {
        viewModel.packages.observe(this, Observer {
            adapter.setData(it)
        })
    }

    private fun openAddEdit(packageId: UUID?) {
        val intent = Intent(this, PackagesPreviewActivity::class.java).apply {
            putExtra(PACKAGE_ID, packageId.toString())
        }
        launcher.launch(intent)
    }
}