package com.vag.lmsapp.app.joborders.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vag.lmsapp.R
import com.vag.lmsapp.app.gallery.picture_browser.PictureCaptureActivity
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.app.gallery.picture_preview.PicturePreviewActivity
import com.vag.lmsapp.databinding.FragmentProofOfPaymentBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.file
import com.vag.lmsapp.util.showConfirmationDialog
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.Instant
import java.util.UUID

@AndroidEntryPoint
class ProofOfPaymentFragment : Fragment() {
    private lateinit var binding: FragmentProofOfPaymentBinding
    private val viewModel: JobOrderPaymentViewModel by activityViewModels()
    private val launcher = FragmentLauncher(this)
    private val fileLauncher = FragmentLauncher(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProofOfPaymentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.paymentId.observe(viewLifecycleOwner, Observer {
            loadProofOfPayment(it)
        })
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is JobOrderPaymentViewModel.DataState.OpenCamera -> {
                    openCamera(it.paymentId)
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.OpenFile -> {
                    browsePictures()
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.RemoveProofOfPayment -> {
                    loadProofOfPayment(null)
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.OpenPromptReplacePictureWithCamera -> {
                    requireContext().showConfirmationDialog("Replace image", "Take a new photo and replace the image? This action cannot be undone!") {
                        openCamera(it.paymentId)
                    }
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.OpenPromptReplacePictureWithFile -> {
                    requireContext().showConfirmationDialog("Replace image", "Browse new picture and replace the image? This action cannot be undone!") {
                        browsePictures()
                    }
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.CopyFile -> {
                    copyImage(it.paymentId, it.uri)
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.OpenImagePreview -> {
                    val intent = Intent(requireContext(), PicturePreviewActivity::class.java).apply {
                        putParcelableArrayListExtra(PicturePreviewActivity.FILENAME_IDS_EXTRA, arrayListOf(PhotoItem(it.paymentId!!, Instant.now())))
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
        binding.buttonCamera.setOnClickListener {
            viewModel.requestOpenCamera()
        }
        binding.buttonBrowsePicture.setOnClickListener {
            viewModel.requestOpenFile()
        }
        binding.buttonClose.setOnClickListener {
            requireContext().showDeleteConfirmationDialog("Remove picture", "Are you sure you want to remove this picture? This action cannot be undone!") {
                viewModel.deletePicture()
            }
        }

        launcher.onOk = {
            it?.getStringExtra(PictureCaptureActivity.URI_ID_EXTRA).toUUID()?.let {
                loadProofOfPayment(it)
            }
        }
        fileLauncher.onOk = {
            viewModel.attachUri(it?.data)
        }
        binding.imageViewProofOfPayment.setOnClickListener {
            viewModel.openImagePreview()
        }
        return binding.root
    }

    fun copyImage(paymentId: UUID, uri: Uri) {
        val name = paymentId.toString()
        val mediaDir = File(context?.filesDir, Constants.PICTURES_DIR)
        val targetFile = File(mediaDir, name)
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val outputStream = FileOutputStream(targetFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        loadProofOfPayment(paymentId)
    }

    private fun loadProofOfPayment(paymentId: UUID?) {
        try {
//            val name = paymentId.toString()
//            val file = File(requireContext().filesDir, Constants.PICTURES_DIR + name)
            val file = requireContext().file(paymentId)
            val uri = FileProvider.getUriForFile(requireContext(), Constants.FILE_PROVIDER, file)

            val requestOptions = RequestOptions()
                .override(240)
                .centerCrop()

            Glide.with(binding.root)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(binding.imageViewProofOfPayment)
        } catch (ioEx: FileNotFoundException) {
            ioEx.printStackTrace()
            ContextCompat.getDrawable(requireContext(), R.drawable.no_preview_available)?.let {
                binding.imageViewProofOfPayment.setImageDrawable(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openCamera(paymentId: UUID?) {
        val intent = Intent(context, PictureCaptureActivity::class.java).apply {
            putExtra(PictureCaptureActivity.URI_ID_EXTRA, paymentId.toString())
        }
        launcher.launch(intent)
    }

    private fun browsePictures() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        fileLauncher.launch(intent)
    }
}