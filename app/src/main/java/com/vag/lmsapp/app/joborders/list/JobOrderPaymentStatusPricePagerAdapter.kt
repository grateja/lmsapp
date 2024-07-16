package com.vag.lmsapp.app.joborders.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.PagerItemJobOrderPaymentStatusPriceBinding

class JobOrderPaymentStatusPricePagerAdapter: RecyclerView.Adapter<JobOrderPaymentStatusPricePagerAdapter.ViewHolder>() {
    private var jobOrderSummary: JobOrderResultSummary? = null

    fun setJobOrderSummary(summary: JobOrderResultSummary) {
        jobOrderSummary = summary
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PagerItemJobOrderPaymentStatusPriceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.amount = when(position) {
            1 -> jobOrderSummary?.paidSum
            2 -> jobOrderSummary?.unpaidSum
            else -> jobOrderSummary?.totalSum
        }
    }

    override fun getItemCount(): Int {
        return 3 // Number of tabs
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(val binding: PagerItemJobOrderPaymentStatusPriceBinding) : RecyclerView.ViewHolder(binding.root)
}