package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.util.DbColumns.Companion.SERVICES

@Entity(tableName = SERVICES)
class EntityService(
    var name: String = "",

    var price: Float = 0f,

    @Embedded
    var serviceRef: EntityServiceRef,

    var hidden: Boolean = false
) : BaseEntity() {
    constructor(machineType: EnumMachineType, serviceType: EnumServiceType, washType: EnumWashType? = null) : this("", 0f, EntityServiceRef(serviceType, machineType, washType, 0))

    @Json(ignore = true)
    var sync: Boolean = false

    fun icon() : Int {
        return serviceRef.washType?.icon ?: serviceRef.serviceType.icon
    }

    fun label() : String {
        return "${serviceRef.machineType.value} $name"
    }
}
