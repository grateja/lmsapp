package com.vag.lmsapp.app.joborders.create.customer

import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.google.android.material.card.MaterialCardView
import java.util.UUID

class CustomersAdapterMinimal : Adapter<CustomerMinimal>(R.layout.recycler_item_customer_minimal) {
    var onEdit: ((CustomerMinimal) -> Unit) ? = null
    fun setSelectedCustomerId(customerId: UUID) {
        list.find {
            it.id == customerId
        }?.let {
            it.selected = true
            notifyItemChanged(list.indexOf(it))
        }
    }
    override fun onBindViewHolder(holder: ViewHolder<CustomerMinimal>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItems()[position]
        holder.itemView.findViewById<MaterialCardView>(R.id.buttonEdit)?.setOnClickListener {
            onEdit?.invoke(item)
        }
    }
}