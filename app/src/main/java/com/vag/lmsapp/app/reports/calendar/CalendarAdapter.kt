package com.vag.lmsapp.app.reports.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.databinding.RecyclerItemCalendarDayBinding
import com.vag.lmsapp.databinding.RecyclerItemCalendarDayEmptyBinding
import com.vag.lmsapp.databinding.RecyclerItemCalendarDayHeaderBinding
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

class CalendarAdapter: RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {
    private var items = mutableListOf<DailyPlaceHolder>()
    var onItemClick: ((LocalDate) -> Unit)? = null
    inner class DayViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: DailyPlaceHolder) {
            when(model) {
                is DailyPlaceHolder.Header -> {
                    binding.setVariable(BR.viewModel, model.dayOfWeek)
                }
                is DailyPlaceHolder.EmptyResult -> {
                    binding.setVariable(BR.viewModel, model.day)
                }
                is DailyPlaceHolder.HasResult -> {
                    binding.setVariable(BR.viewModel, model.result)
                }
                is DailyPlaceHolder.Offset -> {}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = when(viewType) {
            VIEW_TYPE_HAS_RESULT -> {
                RecyclerItemCalendarDayBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            }

            VIEW_TYPE_HEADER -> {
                RecyclerItemCalendarDayHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            }

            else -> {
                RecyclerItemCalendarDayEmptyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            }
        }

        return DayViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if(item is DailyPlaceHolder.HasResult) {
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(item.result.date)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is DailyPlaceHolder.Header -> VIEW_TYPE_HEADER
            is DailyPlaceHolder.Offset -> VIEW_TYPE_OFFSET
            is DailyPlaceHolder.EmptyResult -> VIEW_TYPE_EMPTY_RESULT
            is DailyPlaceHolder.HasResult -> VIEW_TYPE_HAS_RESULT
        }
    }

    fun setData(list: List<DailyPlaceHolder>, currentYear: Int, currentMonth: Int) {
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)

        val yearMonth = YearMonth.of(currentYear, currentMonth)

        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        items.clear()
        items.addAll(
            EnumDayOfWeek.entries.map {
                DailyPlaceHolder.Header(it)
            }
        )

        for (i in 0 until firstDayOfWeek) {
            items.add(DailyPlaceHolder.Offset)  // Empty placeholders for the previous month
        }
        items.addAll(list)
        val totalItems = items.size
        val rowsNeeded = ceil(totalItems / 7.0).toInt()  // Number of rows
        val emptySpacesForNextMonth = rowsNeeded * 7 - totalItems
        // Add empty placeholders for the next month
        for (i in 0 until emptySpacesForNextMonth) {
            items.add(DailyPlaceHolder.Offset)
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_HAS_RESULT = 1
        private const val VIEW_TYPE_EMPTY_RESULT = 2
        private const val VIEW_TYPE_OFFSET = 3
    }
}