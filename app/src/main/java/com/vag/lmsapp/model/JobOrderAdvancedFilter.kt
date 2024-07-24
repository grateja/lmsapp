package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.app.dashboard.data.DateFilter
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobOrderAdvancedFilter(
    var filterBy: EnumJoFilterBy = EnumJoFilterBy.DATE_CREATED,
    var paymentStatus: EnumPaymentStatus = EnumPaymentStatus.ALL,
    override var dateFilter: DateFilter? = null
): Parcelable, BaseFilterParams()