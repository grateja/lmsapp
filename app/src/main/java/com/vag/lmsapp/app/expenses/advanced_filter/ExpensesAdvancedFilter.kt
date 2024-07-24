package com.vag.lmsapp.app.expenses.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpensesAdvancedFilter(
    override var orderBy: String = "Date Created",
) : Parcelable, BaseFilterParams()