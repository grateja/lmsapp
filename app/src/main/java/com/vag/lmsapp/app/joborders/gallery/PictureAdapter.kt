package com.vag.lmsapp.app.joborders.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.databinding.RecyclerItemJobOrderPictureGridBinding
import com.vag.lmsapp.util.Constants.Companion.FILE_PROVIDER
import com.vag.lmsapp.util.Constants.Companion.PICTURES_DIR
import java.io.File
import java.io.FileNotFoundException

class PictureAdapter(private val context: Context) : RecyclerView.Adapter<PictureAdapter.ViewHolder>() {
    private var list: List<PhotoItem> = emptyList()
    inner class ViewHolder(private val binding: RecyclerItemJobOrderPictureGridBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageViewJobOrderPicture
        fun bind(model: PhotoItem) {
            binding.setVariable(BR.viewModel, model)
            try {
                val name = model.id.toString()
                val file = File(context.filesDir, PICTURES_DIR + name)
                val uri = FileProvider.getUriForFile(context, FILE_PROVIDER, file)

                val requestOptions = RequestOptions()
                    .override(240)
                    .centerCrop()

                Glide.with(binding.root)
                    .load(uri)
                    .apply(requestOptions)
                    .into(imageView)
            } catch (ioEx: FileNotFoundException) {
                model.fileDeleted = true
                ioEx.printStackTrace()
                ContextCompat.getDrawable(context, R.drawable.image_deleted)?.let {
                    imageView.setImageDrawable(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var onItemClick: ((PhotoItem) -> Unit) ? = null

    fun setData(pictures: List<PhotoItem>) {
        list = pictures
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemJobOrderPictureGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.bind(r)
        onItemClick?.let { event ->
            holder.itemView.setOnClickListener {
                event.invoke(r)
            }
        }
//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(r)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}