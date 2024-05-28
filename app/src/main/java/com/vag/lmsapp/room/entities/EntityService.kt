package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.util.DbColumns.Companion.SERVICES

@Entity(tableName = SERVICES)
class EntityService(
    var name: String?,

    var price: Float,

    @Embedded
    var serviceRef: EntityServiceRef
) : BaseEntity() {
    constructor(machineType: EnumMachineType) : this(null, 0f, EntityServiceRef(machineType, null, 0))
}
