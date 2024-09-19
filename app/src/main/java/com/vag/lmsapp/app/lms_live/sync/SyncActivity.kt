package com.vag.lmsapp.app.lms_live.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivitySyncBinding
import com.vag.lmsapp.services.BacklogSyncService
import com.vag.lmsapp.services.SyncService
import com.vag.lmsapp.services.SyncService.Companion.ACTION_SYNC_BROADCAST
import com.vag.lmsapp.services.SyncService.Companion.SYNC_BROADCAST_ENTITY_EXTRA
import com.vag.lmsapp.services.SyncService.Companion.SYNC_BROADCAST_PROGRESS_EXTRA
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

        subscribeListeners()
        subscribeEvents()

        val intentFilter = IntentFilter(ACTION_SYNC_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun subscribeListeners() {
        viewModel.pending.observe(this, Observer {
        })
    }

    private fun subscribeEvents() {
        binding.cardButtonSync.setOnClickListener {
            BacklogSyncService.start(this)
        }
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                ACTION_SYNC_BROADCAST -> {
                    val entity = intent.getStringExtra(SYNC_BROADCAST_ENTITY_EXTRA)
                    val progress = intent.getIntExtra(SYNC_BROADCAST_PROGRESS_EXTRA, 0)
                }
            }
        }
    }
}