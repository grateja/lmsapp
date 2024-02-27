package com.vag.lmsapp.app.joborders.create.discount

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.google.android.material.card.MaterialCardView

class DiscountsAdapter : RecyclerView.Adapter<DiscountsAdapter.ViewHolder>() {
    private var list: List<MenuDiscount> = emptyList()
    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MenuDiscount) {
            binding.setVariable(BR.viewModel, model)
        }
    }

    var onItemClick: ((MenuDiscount) -> Unit) ? = null

    fun setData(services: List<MenuDiscount>) {
        list = services
        notifyItemRangeChanged(0, list.size - 1)
    }

    fun notifySelection(discount: MenuDiscount?) {
        deselectAll()
        discount?.let { _discount ->
            list.let {
                it.find { s -> s.discountRefId == _discount.discountRefId }?.apply {
                    selected = true
                    notifyItemChanged(it.indexOf(this))
                }
            }
        }
    }

    private fun deselectAll() {
        list.let {
            it.forEach { discount ->
                if(discount.selected) {
                    discount.selected = false
                    notifyItemChanged(it.indexOf(discount))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_discount,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.bind(r)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(r)
        }
        holder.itemView.apply {
            findViewById<MaterialCardView>(R.id.cardDiscount).also {
                if(r.selected) {
                    it.strokeColor = context.getColor(R.color.card_selected)
                    it.setCardBackgroundColor(context.getColor(R.color.span_background_selected))
                } else {
                    it.strokeColor = context.getColor(R.color.border_primary)
                    it.setCardBackgroundColor(context.getColor(R.color.white))
                }
            }
            findViewById<TextView>(R.id.textTitle).setTextAppearance(
                if(r.selected) {
                    R.style.TextItemTitleActive
                } else {
                    R.style.TextItemTitle
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}