package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType

class EntityServiceRef(
    @ColumnInfo(name = "svc_machine_type")
    var machineType: EnumMachineType,

    @ColumnInfo(name = "svc_wash_type")
    var washType: EnumWashType?,

    @ColumnInfo(name = "svc_minutes")
    var minutes: Int,
) {
    fun pulse() : Int {
        return if(machineType == EnumMachineType.REGULAR_DRYER || machineType == EnumMachineType.TITAN_DRYER) {
            minutes / 10
        } else {
            washType?.pulse ?: 0
        }
    }
}