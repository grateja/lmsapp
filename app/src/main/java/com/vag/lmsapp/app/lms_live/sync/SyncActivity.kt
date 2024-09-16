package com.vag.lmsapp.app.lms_live.sync

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivitySyncBinding
import com.vag.lmsapp.services.BacklogSyncService.Companion.SYNC_NAME_EXTRA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySyncBinding
    private val viewModel: SyncViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sync)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        intent.getStringExtra(SYNC_NAME_EXTRA).let {
            viewModel.setTitle(it)
        }
    }
}