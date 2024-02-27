package com.vag.lmsapp.util

import android.app.DatePickerDialog
import android.content.Context
import java.time.*

class DatePicker(private val context: Context) {
    var onDateSelected: ((LocalDate, String?) -> Unit)? = null

    fun show(date: LocalDate, tag: String? = null) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val localDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                onDateSelected?.invoke(localDate, tag)
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )

        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.show()
    }

    fun setOnDateTimeSelectedListener(listener: (LocalDate, String?) -> Unit) {
        onDateSelected = listener
    }
}