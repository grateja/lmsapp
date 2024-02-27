package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import java.util.*

data class EntityMachineUsageAggrResult(
    @ColumnInfo(name = "machine_id")
    val machineId: UUID,

    @ColumnInfo(name = "machine_type")
    val machineType: EnumMachineType?,

    @ColumnInfo(name = "machine_number")
    val machineNumber: Int,

    @ColumnInfo(name = "count")
    val count: Int,
) {
    fun machineName(): String {
        return machineType?.value + "" + machineNumber
    }
}
