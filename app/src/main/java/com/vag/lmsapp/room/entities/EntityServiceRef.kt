package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType

class EntityServiceRef(
    @Json(name = "svc_service_type")
    @ColumnInfo(name = "svc_service_type")
    var serviceType: EnumServiceType,

    @Json(name = "svc_machine_type")
    @ColumnInfo(name = "svc_machine_type")
    var machineType: EnumMachineType,

    @Json(name = "svc_wash_type")
    @ColumnInfo(name = "svc_wash_type")
    var washType: EnumWashType?,

    @Json(name = "svc_minutes")
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