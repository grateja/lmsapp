package com.vag.lmsapp.app.reports.summary_report.machine_usage

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType

data class SummaryReportMachineUsageSummary(
    val minutes: Int = 0,
    val cycles: Int = 0,

    @ColumnInfo("svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo("svc_service_type")
    val serviceType: EnumServiceType,
) {
    override fun toString(): String {
        return "$machineType ${serviceType}er"
    }
}