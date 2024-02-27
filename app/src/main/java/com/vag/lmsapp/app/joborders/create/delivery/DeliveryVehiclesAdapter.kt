package com.vag.lmsapp.app.joborders.create.delivery

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.google.android.material.card.MaterialCardView
import java.util.*

class DeliveryVehiclesAdapter : RecyclerView.Adapter<DeliveryVehiclesAdapter.ViewHolder>() {
    private var list: List<MenuDeliveryProfile> = emptyList()
    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MenuDeliveryProfile) {
            binding.setVariable(BR.viewModel, model)
        }
    }

    var onItemClick: ((MenuDeliveryProfile) -> Unit) ? = null

    fun setData(services: List<MenuDeliveryProfile>) {
        list = services
        notifyItemRangeChanged(0, list.size - 1)
        println("List size")
        println(list.size)
    }

    fun notifySelection(profileId: UUID?) {
        deselectAll()
        profileId?.let { _profileId ->
            list.let {
                it.find { s -> s.deliveryProfileRefId == _profileId }?.apply {
                    selected = true
                    notifyItemChanged(it.indexOf(this))
                }
            }
        }
    }

    private fun deselectAll() {
        list.let {
            it.forEach { vehicle ->
                if(vehicle.selected) {
                    vehicle.selected = false
                    notifyItemChanged(it.indexOf(vehicle))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_delivery_vehicle,
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
            findViewById<MaterialCardView>(R.id.cardVehicle).also {
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