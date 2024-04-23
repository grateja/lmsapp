package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.util.DbColumns.Companion.MACHINE_REMARKS
import java.util.*

@Entity(tableName = MACHINE_REMARKS)
class EntityMachineRemarks : BaseEntity() {
    @ColumnInfo(name = "machine_id")
    var machineId: UUID? = null

    @ColumnInfo(name = "user_id")
    var userId: UUID? = null
    var title: String? = null
    var remarks: String? = null
}