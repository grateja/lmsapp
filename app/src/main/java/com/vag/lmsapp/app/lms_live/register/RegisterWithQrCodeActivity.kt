package com.vag.lmsapp.app.lms_live.register

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityRegisterWithQrCodeBinding
import com.vag.lmsapp.services.LiveSyncService
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.MoshiHelper
import com.vag.lmsapp.util.PermissionHelper
import com.vag.lmsapp.util.QrCodeAnalyzer
import com.vag.lmsapp.util.showMessageDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterWithQrCodeActivity : AppCompatActivity() {
    @Inject lateinit var moshiHelper: MoshiHelper

    private lateinit var binding: ActivityRegisterWithQrCodeBinding
    private val viewModel: RegisterWithQrCodeViewModel by viewModels()

    private val permissionHelper = object: PermissionHelper(this) {
        override var permissions = arrayOf(Manifest.permission.CAMERA)
    }

    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_with_qr_code)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeListeners()

        if(permissionHelper.hasPermissions()) {
            startCamera()
        } else {
            permissionHelper.requestPermission()
        }
        permissionHelper.setOnRequestGranted {
            startCamera()
        }
    }

    private fun syncSetup() {
        val intent = LiveSyncService.getIntent(this, LiveSyncService.ACTION_SYNC_SETUP, null)
        startForegroundService(intent)
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when (it) {
                is DataState.Invalidate -> {
                    showMessageDialog("Invalid operation", it.message) {
                        startCamera()
                    }
                    viewModel.clearState()
                }
                is DataState.SaveSuccess -> {
                    viewModel.clearState()
                    syncSetup()
                    finish()
                }
                else -> {}
            }
        })
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(binding.previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .build())
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            QrCodeAnalyzer { result ->
                moshiHelper.decodeShopLinkQrCode(result)?.let {
                    viewModel.link(it)
                    stopCamera()
                }
            }
        )
        try {
            cameraProviderFuture.get().bindToLifecycle(
                this, cameraSelector, preview, imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopCamera() {
        try {
            cameraProviderFuture.get().unbindAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}