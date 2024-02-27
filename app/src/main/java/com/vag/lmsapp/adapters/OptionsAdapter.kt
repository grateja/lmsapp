package com.vag.lmsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.google.android.material.card.MaterialCardView

class OptionsAdapter<T>(
    private val layout: Int,
    itemPreset: Array<T>,
    var selectionColor: Int = R.color.primary
) : RecyclerView.Adapter<OptionsAdapter<T>.ViewHolder>() {
    inner class ViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: T) {
            binding.setVariable(BR.viewModel, model)
        }
    }

    inner class OptionWrapper<T>(
        val model: T,
        var selected: Boolean = false,
    )

    var onSelect: ((T) -> Unit)? = null
    val list: MutableList<OptionWrapper<T>> = itemPreset.map {
        OptionWrapper(it)
    }.toMutableList()

    fun setData(items: List<T>) {
        items.forEach {
            list.add(OptionWrapper(it))
        }
        notifyDataSetChanged()
    }

    fun selectOption(deliveryOption: T?) {
        val preSelect = list.find { it.model.toString() == deliveryOption?.toString() && !it.selected }
        preSelect?.let { _preSelect ->
            list.forEach {
                if (it == _preSelect) {
                    it.selected = true
                    onSelect?.invoke(it.model)
                } else {
                    it.selected = false
                }
            }
//            notifyItemChanged(list.indexOf(_preSelect))
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.bind(r.model)
        holder.itemView.setOnClickListener {
            selectOption(r.model)
        }
        holder.itemView.apply {
            val selected = r.selected
            findViewById<TextView>(R.id.textTitle).also {
                if (selected) {
                    it.setTextColor(context.getColor(selectionColor))
                } else {
                    it.setTextColor(context.getColor(R.color.text_dark_grey))
                }
            }
            findViewById<MaterialCardView>(R.id.cardDeliveryOption).also {
                if (selected) {
                    it.strokeColor = context.getColor(selectionColor)
                } else {
                    it.strokeColor = context.getColor(R.color.text_dark_grey)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}