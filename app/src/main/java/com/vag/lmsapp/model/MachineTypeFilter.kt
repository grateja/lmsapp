package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MachineTypeFilter(
    val machineType: EnumMachineType?,
    val serviceType: EnumServiceType?
) : Parcelable {
    override fun toString(): String {
        return "${machineType?.value} ${serviceType?.value}er"
    }
}
