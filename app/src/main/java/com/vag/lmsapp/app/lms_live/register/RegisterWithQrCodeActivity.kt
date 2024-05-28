package com.vag.lmsapp.app.lms_live.register

import android.Manifest
import android.os.Bundle
import android.util.Size
import androidx.activity.enableEdgeToEdge
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityRegisterWithQrCodeBinding
import com.vag.lmsapp.util.PermissionHelper
import com.vag.lmsapp.util.QrCodeAnalyzer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterWithQrCodeActivity : AppCompatActivity() {
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

        if(permissionHelper.hasPermissions()) {
            startCamera()
        } else {
            permissionHelper.requestPermission()
        }
        permissionHelper.setOnRequestGranted {
            startCamera()
        }
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
                viewModel.link(result)
                stopCamera()
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