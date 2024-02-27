package com.vag.lmsapp.app.joborders.payment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemJobOrderMinimalBinding

class JobOrderListPaymentAdapter(val readOnly: Boolean) : RecyclerView.Adapter<JobOrderListPaymentAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: RecyclerItemJobOrderMinimalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jobOrder: JobOrderPaymentMinimal, index: Int) {
            binding.viewModel = jobOrder

            binding.checkbox.setOnClickListener {
                jobOrder.selected = !jobOrder.selected
                onSelectionChange?.invoke(jobOrder)
            }
            binding.card.setOnClickListener {
                onItemClick?.invoke(jobOrder)
//                notifyItemChanged(index)
            }

            if(readOnly) {
                binding.checkbox.visibility = View.GONE
            } else {
                binding.checkbox.visibility = View.VISIBLE
            }
        }
    }

    var onSelectionChange: ((JobOrderPaymentMinimal) -> Unit) ? = null
    var onItemClick: ((JobOrderPaymentMinimal) -> Unit) ? = null

    private var list: MutableList<JobOrderPaymentMinimal> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<JobOrderPaymentMinimal>) {
        list = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemJobOrderMinimalBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun selectedItems() : List<JobOrderPaymentMinimal> {
        return list.filter { it.selected }
    }
}