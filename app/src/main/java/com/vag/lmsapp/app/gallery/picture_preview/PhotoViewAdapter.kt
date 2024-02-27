package com.vag.lmsapp.app.gallery.picture_preview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemPhotoViewBinding
import com.vag.lmsapp.util.Constants.Companion.PICTURES_DIR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PhotoViewAdapter(private val context: Context): RecyclerView.Adapter<PhotoViewAdapter.ViewHolder>() {

    private var photos = emptyList<PhotoItem>()

    var onDeleteInitiate: ((PhotoItem) -> Unit)? = null

    fun setData(ids: List<PhotoItem>) {
        this.photos = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemPhotoViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_photo_view, parent, false)
//        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = photos[position]
        holder.bind(uri)
    }

    override fun getItemCount(): Int = photos.size

    fun removeItem(photo: PhotoItem) {
        val index = photos.indexOf(photo)
        if (index >= 0) {
            val newList = photos.toMutableList()
            newList.removeAt(index)
            photos = newList
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(private val binding: RecyclerItemPhotoViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        private val photoView: PhotoView = itemView.findViewById(R.id.photoView)
//        private val createdAt: TextView = itemView.findViewById(R.id.createdAt)
//        private val buttonDelete: MaterialCardView = itemView.findViewById(R.id.button_delete)

        fun bind(photoItem: PhotoItem) {
            binding.setVariable(BR.viewModel, photoItem)
            binding.textLoading.visibility = View.VISIBLE
            binding.buttonDelete.setOnClickListener {
                onDeleteInitiate?.invoke(photoItem)
            }

            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val file = File(context.filesDir, PICTURES_DIR + photoItem.id)
                    Glide.with(itemView)
                        .load(file)
                        .into(binding.photoView)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                ContextCompat.getDrawable(context, R.drawable.image_deleted)?.let {
                    Glide.with(itemView)
                        .load(it)
                        .into(binding.photoView)
                }
            } finally {
                binding.textLoading.visibility = View.GONE
            }
        }
    }
}