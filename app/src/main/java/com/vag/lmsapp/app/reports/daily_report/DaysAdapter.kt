package com.vag.lmsapp.app.reports.daily_report

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemDayBinding
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class DaysAdapter : RecyclerView.Adapter<DaysAdapter.ViewHolder>() {
    private var onDaySelected: ((LocalDate) -> Unit)? = null

    private val calendar by lazy { Calendar.getInstance() }
    private val calendarForDate by lazy { Calendar.getInstance() }

    private var currentDay = calendar.get(Calendar.DATE)
    private var currentMonth = calendar.get(Calendar.MONTH)
    private var currentYear = calendar.get(Calendar.YEAR)

    private var selectedDay = currentDay
    private var selectedMonth = currentMonth
    private var selectedYear = currentYear

    val items = mutableListOf<Int>()

    private lateinit var recyclerView: RecyclerView

    fun getDayOfWeekAbbreviation(day: Int): String {
        calendarForDate.set(selectedYear, selectedMonth, day) // month is zero-based in Calendar
        val date = calendarForDate.time
        val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
        return sdf.format(date)
    }

    fun isSunday(day: Int): Boolean {
        calendarForDate.set(selectedYear, selectedMonth, day) // month is zero-based in Calendar
        return calendarForDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    inner class ViewHolder(private val binding: RecyclerItemDayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Int/*, selectedDate: Int, today: Int*/) {
            binding.viewModel = model
            if(model == currentDay && currentMonth == selectedMonth && currentYear == selectedYear) {
                binding.card.strokeColor = binding.root.context.getColor(R.color.card_selected)
            } else {
                binding.card.strokeColor = binding.root.context.getColor(R.color.neutral)
            }
            if(model == selectedDay) {
                binding.card.setCardBackgroundColor(binding.root.context.getColor(R.color.card_selected))
                binding.text.setTextColor(binding.root.context.getColor(R.color.white))
            } else {
                binding.card.setCardBackgroundColor(binding.root.context.getColor(R.color.white))
                binding.text.setTextColor(binding.root.context.getColor(R.color.black))
            }

            binding.textDayOfWeek.text = getDayOfWeekAbbreviation(model)
        }
    }

    fun setOnDaySelectedListener(listener: (LocalDate) -> Unit) {
        onDaySelected = listener
    }

    fun setMonth(month: Int) {
        selectedMonth = month
        fillDays()
        invokeDate()
    }

    private fun fillDays() {
        val daysInMonth = getNumberOfDaysInMonth()

        if(items.isEmpty()) {
            for (day in 1..daysInMonth) {
                items.add(day)
            }
            notifyItemRangeInserted(0, daysInMonth - 1)
        } else {
            val last = items.last()
            if(last < daysInMonth) {
                for(day in (last + 1) .. daysInMonth) {
                    items.add(day)
                }
                notifyItemRangeInserted(last, daysInMonth - last)
            } else if(last > daysInMonth) {
                val startRemoveIndex = items.indexOf(daysInMonth)
                if (startRemoveIndex != -1) {
                    val countToRemove = items.size - startRemoveIndex
                    for (i in 0 until countToRemove) {
                        items.removeAt(items.size - 1)
                    }
                    notifyItemRangeRemoved(startRemoveIndex, countToRemove)
                }
            }
        }

        scrollToPosition(currentDay - 1)
    }

    private fun scrollToPosition(position: Int) {
        recyclerView.let {
            it.post {
                // Force the ViewHolder to be created and laid out
                val viewHolder = it.findViewHolderForAdapterPosition(position)
                if (viewHolder == null) {
                    // If the view holder is null, the item might not be laid out yet.
                    // Scroll to the position first to ensure it gets laid out.
                    it.scrollToPosition(position)
                    it.post {
                        // Now that the item is laid out, we can get its width
                        val viewHolderPost = it.findViewHolderForAdapterPosition(position)
                        val itemWidth = viewHolderPost?.itemView?.width ?: 0
                        val recyclerViewWidth = it.width
                        val offset = (recyclerViewWidth - itemWidth) / 2

                        val layoutManager = it.layoutManager as LinearLayoutManager
                        layoutManager.scrollToPositionWithOffset(position, offset)
                    }
                } else {
                    // If the view holder is already available
                    val itemWidth = viewHolder.itemView.width
                    val recyclerViewWidth = it.width
                    val offset = (recyclerViewWidth - itemWidth) / 2

                    val layoutManager = it.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(position, offset)
                }
            }
        }
    //        this.recyclerView.let {
//            it.post {
//                val recyclerViewWidth = it.width
//                val viewHolder = it.findViewHolderForAdapterPosition(position)
//                val itemWidth = viewHolder?.itemView?.width ?: 0
//                val offset = (recyclerViewWidth - itemWidth) / 2
//
//                val layoutManager = it.layoutManager as LinearLayoutManager
//                layoutManager.scrollToPositionWithOffset(position, offset)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemDayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_day,
            parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selected = items[position]
        holder.bind(selected/*, selectedDay, currentDay*/)

        holder.itemView.setOnClickListener {
            val currentPosition = items.indexOf(selectedDay)
            selectedDay = selected
            notifyItemChanged(position)
            notifyItemChanged(currentPosition)
            invokeDate()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    private fun getNumberOfDaysInMonth(): Int {
        val yearMonth = YearMonth.of(selectedYear, selectedMonth + 1)
        return yearMonth.lengthOfMonth()
    }

    fun setYear(year: Int) {
        selectedYear = year
        fillDays()
        invokeDate()
    }

    private fun invokeDate() {
        if(selectedDay > getNumberOfDaysInMonth()) {
            selectedDay = getNumberOfDaysInMonth()
        }
        onDaySelected?.invoke(
            LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
        )
    }

    fun setDate(localDate: LocalDate) {
        val currentIndex = this.selectedDay - 1
        val nextIndex = localDate.dayOfMonth - 1

        this.selectedDay = localDate.dayOfMonth
        this.selectedYear = localDate.year
        this.selectedMonth = localDate.monthValue - 1

        fillDays()
        invokeDate()

        notifyItemChanged(currentIndex)
        notifyItemChanged(nextIndex)
    }
}