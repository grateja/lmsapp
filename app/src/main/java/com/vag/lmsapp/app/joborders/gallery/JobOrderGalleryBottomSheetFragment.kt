package com.vag.lmsapp.app.joborders.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.gallery.picture_browser.PictureCaptureActivity
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.app.gallery.picture_preview.PicturePreviewActivity
import com.vag.lmsapp.databinding.FragmentJobOrderGalleryBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.FragmentLauncher
import com.vag.lmsapp.util.setGridLayout
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.UUID

@AndroidEntryPoint
class JobOrderGalleryBottomSheetFragment : BaseModalFragment() {
    override var fullHeight = true

    private val viewModel: JobOrderGalleryViewModel by viewModels()
    private lateinit var binding: FragmentJobOrderGalleryBottomSheetBinding
    private lateinit var pictureListAdapter: PictureAdapter
//    private lateinit var picturePreviewAdapter: PhotoViewAdapter
    private val cameraLauncher = FragmentLauncher(this)
    private val fileLauncher = FragmentLauncher(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobOrderGalleryBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        pictureListAdapter = PictureAdapter(requireContext())
//        picturePreviewAdapter = PhotoViewAdapter(requireContext())

        binding.recyclerJobOrderGallery.adapter = pictureListAdapter
        binding.recyclerJobOrderGallery.setGridLayout(requireContext(), 100.dp)

        arguments?.getString(JOB_ORDER_ID)?.toUUID()?.let {
            viewModel.setJobOrderId(it)
        }

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonAddPicture.setOnClickListener {
            openCamera()
        }
        binding.buttonBrowsePicture.setOnClickListener {
            browsePictures()
        }
        pictureListAdapter.onItemClick = {
            if(it.fileDeleted) {
                requireContext().showDeleteConfirmationDialog("File deleted or corrupted", "Delete this file permanently?") {
                    viewModel.removePicture(it.id)
                }
            } else {
                viewModel.openPreview(it)
            }
        }

        cameraLauncher.onOk = {
            it.data?.getStringExtra(PictureCaptureActivity.URI_ID_EXTRA)?.let {
                viewModel.attachPicture(UUID.fromString(it))
            }
        }
        fileLauncher.onOk = {
            val imageUris = mutableListOf<Uri>()
            val names = mutableListOf<UUID>()

            it.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    imageUris.add(uri)
                }
            }

            val mediaDir = File(context?.filesDir, Constants.PICTURES_DIR)

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

    private fun subscribeListeners() {
        viewModel.jobOrderPictures.observe(viewLifecycleOwner, Observer {
            pictureListAdapter.setData(it)
        })
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is JobOrderGalleryViewModel.NavigationState.PreviewPicture -> {
                    openPreview(it.photoItems, it.index)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
    private fun openPreview(uriIds: List<PhotoItem>, index: Int) {
        val intent = Intent(context, PicturePreviewActivity::class.java).apply {
            putParcelableArrayListExtra(PicturePreviewActivity.FILENAME_IDS_EXTRA, ArrayList(uriIds))
            putExtra(PicturePreviewActivity.INDEX, index)
        }
        startActivity(intent)
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

    companion object {
        fun newInstance(jobOrderId: UUID): JobOrderGalleryBottomSheetFragment {
            return JobOrderGalleryBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(JOB_ORDER_ID, jobOrderId.toString())
                }
            }
        }
    }
}