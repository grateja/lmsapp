package com.vag.lmsapp.app.customers.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomersAdvancedFilter(
    override var orderBy: String = "Name",
) : Parcelable, BaseFilterParams()