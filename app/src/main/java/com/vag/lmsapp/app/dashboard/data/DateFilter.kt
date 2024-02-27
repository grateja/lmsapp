package com.vag.lmsapp.app.dashboard.data

import android.os.Parcelable
import com.vag.lmsapp.util.toShort
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Parcelize
data class DateFilter(
    var dateFrom: LocalDate,
    var dateTo: LocalDate?
) : Parcelable {
    constructor() : this(LocalDate.now(), LocalDate.now())
    constructor(localDate: LocalDate) : this(localDate, localDate)

    override fun toString(): String {
        val dateFromString = dateFrom.toShort()
        val dateToString = dateTo?.toShort()

        return if (dateTo != null) {
            "$dateFromString to $dateToString"
        } else {
            dateFromString
        } + (
            dateTo?.let {
                dateTo?.let {
                    val days = ChronoUnit.DAYS.between(dateFrom, dateTo)
                    " (${days + 1} days)"
                } ?: ""
            } ?: ""
        )
    }
}
