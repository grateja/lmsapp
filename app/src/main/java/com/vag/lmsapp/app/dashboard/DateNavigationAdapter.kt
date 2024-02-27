package com.vag.lmsapp.app.dashboard

import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import java.time.LocalDate

class DateNavigationAdapter : Adapter<LocalDate>(R.layout.recycler_item_date_navigation) {
    fun setCurrentDate(date: LocalDate) {
        try {
            list.clear()
            for (i in -2 .. 2) {
                list.add(date.plusDays(i.toLong()))
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateItems(position: Int) {
        if(position < 2) {
            addToFirst()
        } else if(position > 2) {
            addToLast()
        }
    }

    private fun addToLast() {
        list.last().plusDays(1).let {
            list.add(it)
        }
        list.removeFirst()
        notifyItemRemoved(0)
        notifyItemInserted(4)
    }

    private fun addToFirst() {
        list.first().plusDays(-1).let {
            list.add(0, it)
        }
        list.removeLast()
        notifyItemRemoved(4)
        notifyItemInserted(0)
    }
}