package com.vag.lmsapp.app.joborders.create.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.gallery.picture_browser.PictureCaptureActivity
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.app.gallery.picture_preview.PicturePreviewActivity
import com.vag.lmsapp.app.joborders.create.CreateJobOrderViewModel
import com.vag.lmsapp.databinding.FragmentJobOrderCreateGalleryBinding
import com.vag.lmsapp.util.Constants.Companion.PICTURES_DIR
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import java.io.File
import java.io.FileOutputStream
import java.util.*

class CreateJobOrderGalleryFragment : Fragment() {
    private val viewModel: CreateJobOrderViewModel by activityViewModels()
    private lateinit var binding: FragmentJobOrderCreateGalleryBinding
    private lateinit var adapter: PictureAdapter

    private val cameraLauncher = FragmentLauncher(this)
    private val fileLauncher = FragmentLauncher(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJobOrderCreateGalleryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = PictureAdapter(requireContext())

        binding.recyclerJobOrderGallery.adapter = adapter

        viewModel.jobOrderPictures.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

        subscribeEvents()
        subscribeListener()
        return binding.root
    }

    private fun subscribeListener() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is CreateJobOrderViewModel.DataState.OpenCamera -> {
                    openCamera()
                    viewModel.resetState()
                }
                is CreateJobOrderViewModel.DataState.OpenPictures -> {
                    openPreview(it.ids, it.position)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonAddPicture.setOnClickListener {
            viewModel.openCamera()
        }

        binding.buttonBrowsePicture.setOnClickListener {
            browsePictures()
        }

        adapter.onItemClick = {
            if(it.fileDeleted) {
                requireContext().showDeleteConfirmationDialog("File deleted or corrupted", "Delete this file permanently?") {
                    viewModel.removePicture(it.id)
                }
            } else {
                viewModel.openPictures(it.id)
            }
        }

        cameraLauncher.onOk = {
            it?.getStringExtra(PictureCaptureActivity.URI_EXTRA)?.let {
                viewModel.attachPicture(UUID.fromString(it))
            }
        }
        fileLauncher.onOk = {
            val imageUris = mutableListOf<Uri>()
            val names = mutableListOf<UUID>()

            it?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    imageUris.add(uri)
                }
            }

            val mediaDir = File(context?.filesDir, PICTURES_DIR)

            imageUris.forEach { _uri ->
                val name = UUID.randomUUID().toString()
                val targetFile = File(mediaDir, name)
                val inputStream = context?.contentResolver?.openInputStream(_uri)
                val outputStream = FileOutputStream(targetFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input?.copyTo(output)
                        names.add(UUID.fromString(name))
                    }
                }
            }

            viewModel.attachPictures(names)
        }
    }

    private fun openCamera() {
        val intent = Intent(context, PictureCaptureActivity::class.java)
        cameraLauncher.launch(intent)
    }

    private fun browsePictures() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        fileLauncher.launch(intent)
    }

    private fun openPreview(uriIds: List<PhotoItem>, index: Int) {
        val intent = Intent(context, PicturePreviewActivity::class.java).apply {
            putParcelableArrayListExtra(PicturePreviewActivity.FILENAME_IDS_EXTRA, ArrayList(uriIds))
            putExtra(PicturePreviewActivity.INDEX, index)
        }
        startActivity(intent)
    }
}