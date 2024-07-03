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
        return if(serviceType == EnumServiceType.DRY) {
            minutes / 10
        } else if(serviceType == EnumServiceType.WASH) {
            washType?.pulse ?: 0
        } else {
            0
        }
    }

    fun description(): String {
        return "$minutes minutes $washType"
    }
}