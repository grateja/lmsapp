package com.vag.lmsapp.app.reports.yearly_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemMonthlyResultBinding
import com.vag.lmsapp.databinding.RecyclerItemYearlyResultBinding
import java.time.LocalDate

class YearlyResultAdapter: RecyclerView.Adapter<YearlyResultAdapter.ViewHolder>() {
    var onItemClick: ((YearlyResult) -> Unit)? = null
    private var items = mutableListOf<YearlyResult>()
    inner class ViewHolder(val binding: RecyclerItemYearlyResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: YearlyResult) {
            binding.viewModel = model

//            val thisMonth = LocalDate.now().month.value == model.month.monthNumberStr.toInt()
//            if(thisMonth) {
//                binding.extra.text = "Today"
//            } else {
//                binding.extra.text = ""
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemYearlyResultBinding.inflate(
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

    fun setData(list: List<YearlyResult>) {
        items = list.toMutableList()
        notifyDataSetChanged()
    }
}