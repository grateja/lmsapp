package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumPaymentStatus

object PaymentStatusConverter {
    @TypeConverter
    fun fromPaymentStatus(paymentStatus: EnumPaymentStatus?): Int? {
        return paymentStatus?.id
    }

    @TypeConverter
    fun paymentStatusFromInt(id: Int?): EnumPaymentStatus? {
        return if(id == null) {
            null
        } else {
            EnumPaymentStatus.fromId(id)
        }
    }
}