package com.vag.lmsapp.app.joborders.create.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.google.android.material.card.MaterialCardView

class JobOrderProductsItemAdapter: RecyclerView.Adapter<JobOrderProductsItemAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val removeButton: ImageButton = binding.root.findViewById(R.id.button_remove)
        val cardItem: MaterialCardView = binding.root.findViewById(R.id.card_item)
        fun bind(model: MenuProductItem) {
            binding.setVariable(BR.viewModel, model)
        }
    }

    private var list: MutableList<MenuProductItem> = mutableListOf()

    var onItemClick: ((MenuProductItem) -> Unit) ? = null
    var onDeleteRequest: ((MenuProductItem) -> Unit) ? = null
//    var locked: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_create_job_order_selected_product,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val r = list[position]
        holder.bind(r)

        holder.cardItem.setOnClickListener {
            onItemClick?.invoke(r)
        }

//        holder.removeButton.visibility = if(locked) View.GONE else View.VISIBLE
        holder.removeButton.setOnClickListener {
            onDeleteRequest?.invoke(r)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(jobOrderProducts: MutableList<MenuProductItem>) {
        list = jobOrderProducts.filter { it.deletedAt == null }.toMutableList()
//        notifyItemRangeChanged(0, jobOrderProducts.size - 1)
        notifyDataSetChanged()
    }

//    fun lock(value: Boolean) {
//        locked = value
//        notifyDataSetChanged()
//    }
}