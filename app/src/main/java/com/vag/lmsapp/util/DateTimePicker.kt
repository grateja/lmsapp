package com.vag.lmsapp.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateTimePicker(private val context: Context) {
    private lateinit var zonedDateTime: ZonedDateTime
    var onDateTimeSelected: ((Instant) -> Unit)? = null

    fun show(instant: Instant = Instant.now()) {
        zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, selectedHourOfDay, selectedMinute ->
                        val localDateTime = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDayOfMonth, selectedHourOfDay, selectedMinute)
                        onDateTimeSelected?.invoke(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                    },
                    zonedDateTime.hour,
                    zonedDateTime.minute,
                    false
                )

                timePickerDialog.show()
            },
            zonedDateTime.year,
            zonedDateTime.monthValue - 1,
            zonedDateTime.dayOfMonth
        )

        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.show()
    }

    fun setOnDateTimeSelectedListener(listener: (Instant) -> Unit) {
        onDateTimeSelected = listener
    }
}