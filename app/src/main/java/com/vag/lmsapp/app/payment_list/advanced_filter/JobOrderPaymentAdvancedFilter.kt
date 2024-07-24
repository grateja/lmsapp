package com.vag.lmsapp.app.payment_list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobOrderPaymentAdvancedFilter(
    override var orderBy: String = "Date Paid",
) : Parcelable, BaseFilterParams()