package com.vag.lmsapp.app.reports.monthly_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemMonthlyResultBinding
import com.vag.lmsapp.databinding.RecyclerItemMonthlyResultEmptyBinding
import java.time.LocalDate

class MonthlyResultAdapter: RecyclerView.Adapter<MonthlyResultAdapter.ViewHolder>() {
    var onItemClick: ((MonthlyResult) -> Unit)? = null
    private var items = mutableListOf<MonthlyPlaceHolder>()
    inner class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MonthlyPlaceHolder) {
            when(model) {
                is MonthlyPlaceHolder.HasResult -> {
                    binding.setVariable(BR.viewModel, model.result)
                    val thisMonth = LocalDate.now().month.value == model.result.month.monthNumberStr.toInt()
                }

                is MonthlyPlaceHolder.EmptyResult -> {
                    binding.setVariable(BR.viewModel, model.month)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is MonthlyPlaceHolder.EmptyResult -> VIEW_TYPE_EMPTY_RESULT
            is MonthlyPlaceHolder.HasResult -> VIEW_TYPE_HAS_RESULT
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when(viewType) {
            VIEW_TYPE_HAS_RESULT -> {
                RecyclerItemMonthlyResultBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            }

            else -> {
                RecyclerItemMonthlyResultEmptyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            }
        }

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = items[position]
        holder.bind(result)

        if(result is MonthlyPlaceHolder.HasResult) {
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(result.result)
            }
        }
    }

    fun setData(list: List<MonthlyPlaceHolder>) {
        items = list.toMutableList()
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_HAS_RESULT = 1
        private const val VIEW_TYPE_EMPTY_RESULT = 2
    }
}