package com.vag.lmsapp.app.joborders.create.extras

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

class JobOrderExtrasItemAdapter: RecyclerView.Adapter<JobOrderExtrasItemAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val removeButton: ImageButton = binding.root.findViewById(R.id.button_remove)
        val cardItem: MaterialCardView = binding.root.findViewById(R.id.card_item)
        fun bind(model: MenuExtrasItem) {
            binding.setVariable(BR.viewModel, model)
        }
    }

    private var list: MutableList<MenuExtrasItem> = mutableListOf()

    var onItemClick: ((MenuExtrasItem) -> Unit) ? = null
    var onDeleteRequest: ((MenuExtrasItem) -> Unit) ? = null
    var locked: Boolean = false

    fun setData(services: List<MenuExtrasItem>) {
        list = services.filter { it.deletedAt == null }.toMutableList()
//        notifyItemRangeChanged(0, services.size -1)
        notifyDataSetChanged()
    }
//    fun addItem(service: MenuExtrasItem) {
//        list.let { _list ->
//            _list.find { s -> s.id == service.id }?.let {
//                notifyItemChanged(_list.indexOf(it))
//            }
//        }
//    }
//    fun removeItem(index: Int) {
//        println("remove item size")
//        println(list.size)
//        notifyItemRemoved(index)
//        list.let { _list ->
//            println("find service")
//            _list.find { s -> s.id == service.id }?.let {
//                println("you wish")
//                notifyItemRemoved(_list.indexOf(it))
//            }
//        }
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_create_job_order_selected_extras,
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
        holder.removeButton.visibility = if(locked) View.GONE else View.VISIBLE
        holder.removeButton.setOnClickListener {
            onDeleteRequest?.invoke(r)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun lock(value: Boolean) {
        locked = value
        notifyDataSetChanged()
    }
}