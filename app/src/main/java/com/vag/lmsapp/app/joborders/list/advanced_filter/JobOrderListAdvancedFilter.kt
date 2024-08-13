package com.vag.lmsapp.app.joborders.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.model.EnumJoFilterBy
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.util.DateFilter
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobOrderListAdvancedFilter(
    var filterBy: EnumJoFilterBy = EnumJoFilterBy.DATE_CREATED,
    var paymentStatus: EnumPaymentStatus = EnumPaymentStatus.ALL,
    override var dateFilter: DateFilter? = null
): Parcelable, BaseFilterParams()