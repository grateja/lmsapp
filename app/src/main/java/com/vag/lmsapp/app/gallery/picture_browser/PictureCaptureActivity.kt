package com.vag.lmsapp.app.gallery.picture_browser

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityPictureCaptureBinding
import com.vag.lmsapp.util.Constants.Companion.PICTURES_DIR
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class PictureCaptureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureCaptureBinding

    private val viewModel: PictureCaptureViewModel by viewModels()

    private lateinit var cameraController: LifecycleCameraController
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picture_capture)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.progressBar.visibility = View.GONE

        subscribeEvents()
        subscribeListeners()
        if(!hasPermissions(baseContext)) {
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        } else {
            startCameraController()
        }
    }

    private fun takePhoto() {
        val mediaDir = File(filesDir, PICTURES_DIR)
        val name = UUID.randomUUID().toString()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(File(mediaDir, name)).build()

        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModel.setUri(name)
//                    val id = ContentUris.parseId(outputFileResults.savedUri!!)
//                    binding.root.showSnackBar(outputFileResults.savedUri.toString())
                    viewModel.setCapturing(false)

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        binding.imagePreview.setImageURI(outputFileResults.savedUri)
//                    }
//                    binding.progressBar.visibility = View.GONE
//                    binding.imagePreview.visibility = View.VISIBLE
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    viewModel.setCapturing(false)
                }
            }
        )

        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
//                    val bitmap = image.toBitmap()
//                    binding.imagePreview.setImageBitmap(bitmap)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle capture error
                }
            })
    }

//    private fun getOutputDirectory(): File {
//        val mediaDir = File(filesDir, PICTURES_DIR)
//
//        if (!mediaDir.exists() && !mediaDir.mkdirs()) {
//            // Handle directory creation failure
//        }
//
//        return mediaDir
//    }

    private fun startCameraController() {
        cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        binding.cameraView.controller = cameraController
    }

//    private suspend fun startCameraProvider() {
//        val cameraProvider = ProcessCameraProvider.getInstance(this).await()
//
//        val preview = Preview.Builder().build().apply {
//            setSurfaceProvider(binding.cameraView.surfaceProvider)
//        }
//
//        imageCapture = ImageCapture.Builder().build()
//
//        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//        try {
//            cameraProvider.unbindAll()
//            val camera = cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageCapture
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if(it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = true
        }
        if(!permissionGranted) {
            showDialog("Camera permission denied")
        } else {
            startCameraController()
//            lifecycleScope.launch {
//                startCameraProvider()
//            }
        }
    }

    companion object {
        //        const val JOB_ORDER_ID_EXTRA = "jobOrderId"
        const val URI_EXTRA = "uri"
        //        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }.toTypedArray()
        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun subscribeEvents() {
        binding.buttonCamera.setOnClickListener {
            viewModel.setCapturing(true)
            takePhoto()
        }

        binding.buttonOk.setOnClickListener {
            viewModel.submit()
        }

        binding.buttonDiscard.setOnClickListener {
            showDeleteConfirmationDialog("You have unsaved picture", "Do you want to discard them?") {
                viewModel.discard()
            }
        }
    }

    private fun loadImage(fileName: String) {
        val file = File(filesDir, "pictures/$fileName")
        Glide.with(binding.root)
            .load(file)
            .into(binding.imagePreview)
    }

    private fun subscribeListeners() {
        viewModel.uri.observe(this, Observer {
            if(it != null) {
                loadImage(it)
            }
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is PictureCaptureViewModel.NavigationState.Capture -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(URI_EXTRA, it.uri)
                    })
                    viewModel.resetState()
                    finish()
                }
                is PictureCaptureViewModel.NavigationState.Discard -> {
                    viewModel.resetState()
                }
                is PictureCaptureViewModel.NavigationState.RequestExit -> {
                    if(it.canExit) {
                        finish()
                    } else {
                        showDeleteConfirmationDialog("You have unsaved picture", "Do you want to discard them?") {
                            viewModel.discard()
                            finish()
                        }
                    }
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.requestExit()
    }
}