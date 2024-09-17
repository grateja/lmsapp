package com.vag.lmsapp.app.lms_live.sync

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivitySyncBinding
import com.vag.lmsapp.services.BacklogSyncService
import com.vag.lmsapp.services.SyncService.Companion.SYNC_NAME_EXTRA
import com.vag.lmsapp.util.Constants.Companion.ACTION_STOP_SERVICE
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

//        val pending = when(intent.getStringExtra(SYNC_NAME_EXTRA)) {
//            BacklogSyncService.NAME -> Intent(this, BacklogSyncService::class.java)
//            else -> null
//        }?.apply {
//            action = ACTION_STOP_SERVICE
//        }
//
//        stopService(pending)
    }
}