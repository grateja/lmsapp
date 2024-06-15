package com.vag.lmsapp.app.daily_report.wash_dry_services

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType

data class DailyReportWashDryService(
    @ColumnInfo(name = "service_name")
    val name: String?,
    val count: Int,

    @ColumnInfo(name = "svc_machine_type")
    val machineType: EnumMachineType?,

    @ColumnInfo(name = "svc_wash_type")
    val washType: EnumWashType?,
) {
    override fun toString() : String {
        return "($count) ${machineType?.abbr} $name"
    }
}
