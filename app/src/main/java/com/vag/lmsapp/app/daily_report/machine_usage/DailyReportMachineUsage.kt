package com.vag.lmsapp.app.daily_report.machine_usage

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType

data class DailyReportMachineUsage(
    @ColumnInfo("machine_number")
    val machineNumber: Int,

    @ColumnInfo("machine_type")
    val machineType: EnumMachineType,

    val count: Int
) {
    override fun toString(): String {
        return "$machineType $machineNumber"
    }
}