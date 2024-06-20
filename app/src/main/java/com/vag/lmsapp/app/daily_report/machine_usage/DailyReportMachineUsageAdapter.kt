package com.vag.lmsapp.app.daily_report.machine_usage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemDailyReportMachineUsageBinding

class DailyReportMachineUsageAdapter: RecyclerView.Adapter<DailyReportMachineUsageAdapter.ViewHolder>() {
    private var items: List<DailyReportMachineUsage> = listOf()
    inner class ViewHolder(val binding: RecyclerItemDailyReportMachineUsageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: DailyReportMachineUsage) {
            binding.setVariable(BR.viewModel, model)
            binding.text.text = binding.root.context.resources.getQuantityString(R.plurals.cycles, model.count, model.count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemDailyReportMachineUsageBinding.inflate(
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
    }

    fun setData(items: List<DailyReportMachineUsage>) {
        this.items = items
        notifyDataSetChanged()
    }
}