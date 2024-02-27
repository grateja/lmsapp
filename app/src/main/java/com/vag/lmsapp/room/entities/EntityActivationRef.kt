package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

data class EntityActivationRef(
    @ColumnInfo(name = "time_activated")
    var timeActivated: Instant?,

    @ColumnInfo(name = "total_minutes")
    var totalMinutes: Int?,

    @ColumnInfo(name = "jo_service_id")
    var joServiceId: UUID?,

    @ColumnInfo(name = "customer_id")
    var customerId: UUID?,
) {
    fun remainingSeconds() : Long {
        val currentTime = Instant.now()
        val totalMinutes = totalMinutes?.toLong() ?: 0
        val timeActivated = timeActivated?.plus(totalMinutes, ChronoUnit.MINUTES)

        return if(timeActivated != null) {
            Duration.between(currentTime, timeActivated).seconds
        } else {
            0
        }
    }

    fun remainingTime() : Long {
        return remainingSeconds() / 60
//        val totalMinutes = this.totalMinutes ?: 0
//
//        timeActivated?.let {
//            return ChronoUnit.MINUTES.between(Instant.now(), it.plus(totalMinutes.toLong(), ChronoUnit.MINUTES)) + 1
//        }
//        return 0
    }

    fun delayInMillis() : Long {
        val totalMinutes = this.totalMinutes ?: 0

        timeActivated?.let {
            return ChronoUnit.MILLIS.between(Instant.now(), it.plus(totalMinutes.toLong(), ChronoUnit.MINUTES))
        }
        return 0
    }

    fun running() : Boolean {
        return this.delayInMillis() > 0
    }
}