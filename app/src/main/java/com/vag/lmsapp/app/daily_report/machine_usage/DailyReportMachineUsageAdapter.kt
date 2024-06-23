package com.vag.lmsapp.app.daily_report.machine_usage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.compose.ui.unit.dp
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemDailyReportMachineUsageBinding
import com.vag.lmsapp.util.toPixels


class DailyReportMachineUsageAdapter: RecyclerView.Adapter<DailyReportMachineUsageAdapter.ViewHolder>() {
    private var items: List<DailyReportMachineUsageSummary> = listOf()
    inner class ViewHolder(val binding: RecyclerItemDailyReportMachineUsageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: DailyReportMachineUsageSummary) {
            binding.setVariable(BR.viewModel, model)
            binding.textUsage.text = binding.root.context.resources.getQuantityString(R.plurals.cycles, model.cycles, model.cycles)
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

    fun setData(items: List<DailyReportMachineUsageSummary>) {
        this.items = items
        notifyDataSetChanged()
    }
}