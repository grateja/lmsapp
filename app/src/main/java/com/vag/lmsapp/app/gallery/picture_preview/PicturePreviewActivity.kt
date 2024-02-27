package com.vag.lmsapp.app.gallery.picture_preview

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityPicturePreviewBinding
import com.vag.lmsapp.util.Constants.Companion.PICTURES_DIR
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PicturePreviewActivity : AppCompatActivity() {
    companion object {
        const val FILENAME_IDS_EXTRA = "fileNameIds"
        const val INDEX = "initialId"
    }

    private lateinit var binding: ActivityPicturePreviewBinding
    private val viewModel: PicturePreviewViewModel by viewModels()

    private lateinit var adapter: PhotoViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picture_preview)
        binding.lifecycleOwner = this

        adapter = PhotoViewAdapter(applicationContext)
        binding.mainContainer.adapter = adapter

        subscribeListeners()
        subscribeEvents()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(FILENAME_IDS_EXTRA, PhotoItem::class.java)?.let {
                viewModel.setUriIds(it.toList())
            }
        } else {
            intent.getParcelableArrayListExtra<PhotoItem>(FILENAME_IDS_EXTRA)?.let {
                viewModel.setUriIds(it)
            }
        }
    }

    private fun subscribeEvents() {
        adapter.onDeleteInitiate = {
            showDeleteConfirmationDialog("You have unsaved picture", "Do you want to discard them?") {
                viewModel.deletePicture(it)
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.uriIds.observe(this, Observer {
            adapter.setData(it)
            intent.getIntExtra(INDEX, 0).let { _index ->
                binding.mainContainer.setCurrentItem(_index, false)
            }
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is PicturePreviewViewModel.DataState.RemovePicture -> {
                    try {
                        File(filesDir, PICTURES_DIR + it.photoItem.id).let { _file ->
                            if(_file.exists()) {
                                _file.delete()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        adapter.removeItem(it.photoItem)
                    }
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}