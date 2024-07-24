package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.app.dashboard.data.DateFilter
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobOrderPaymentAdvancedFilter(
    override var orderBy: String = "Date Paid",
) : Parcelable, BaseFilterParams()