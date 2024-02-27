package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType

data class EntityJobOrderServiceAggrResult(
    @ColumnInfo(name = "service_name")
    val name: String?,
    val count: String,

    @ColumnInfo(name = "svc_machine_type")
    val machineType: EnumMachineType?,

    @ColumnInfo(name = "svc_wash_type")
    val washType: EnumWashType?,
) {
    override fun toString() : String {
        return "${machineType?.abbr} $name"
    }
}
