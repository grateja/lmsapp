package com.vag.lmsapp.app.reports.summary_report.machine_usage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemSummaryReportMachineUsageBinding


class SummaryReportMachineUsageAdapter: RecyclerView.Adapter<SummaryReportMachineUsageAdapter.ViewHolder>() {
    var onItemClick: ((SummaryReportMachineUsageSummary) -> Unit)? = null
    private var items: List<SummaryReportMachineUsageSummary> = listOf()
    inner class ViewHolder(val binding: RecyclerItemSummaryReportMachineUsageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SummaryReportMachineUsageSummary) {
            binding.setVariable(BR.viewModel, model)
            binding.textUsage.text = binding.root.context.resources.getQuantityString(R.plurals.cycles, model.cycles, model.cycles)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemSummaryReportMachineUsageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    fun setData(items: List<SummaryReportMachineUsageSummary>) {
        this.items = items
        notifyDataSetChanged()
    }
}