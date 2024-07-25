package com.vag.lmsapp.app.app_settings.user.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.model.Role
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAccountAdvancedFilter(
    var role: Role? = null
): BaseFilterParams(), Parcelable
