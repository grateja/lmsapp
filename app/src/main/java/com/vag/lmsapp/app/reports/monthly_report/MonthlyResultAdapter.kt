package com.vag.lmsapp.app.reports.monthly_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemMonthlyResultBinding
import java.time.LocalDate

class MonthlyResultAdapter: RecyclerView.Adapter<MonthlyResultAdapter.ViewHolder>() {
    var onItemClick: ((MonthlyResult) -> Unit)? = null
    private var items = mutableListOf<MonthlyResult>()
    inner class ViewHolder(val binding: RecyclerItemMonthlyResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MonthlyResult) {
            binding.viewModel = model

            val thisMonth = LocalDate.now().month.value == model.month.monthNumberStr.toInt()
            if(thisMonth) {
                binding.extra.text = "Today"
            } else {
                binding.extra.text = ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemMonthlyResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = items[position]
        holder.bind(result)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(result)
        }
    }

    fun setData(list: List<MonthlyResult>) {
        items = list.toMutableList()
        notifyDataSetChanged()
    }
}