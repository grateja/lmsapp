package com.vag.lmsapp.app.machines.preview.machine_usages

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.util.EnumSortDirection
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class MachineUsageAdvancedFilter(
    var machineType: EnumMachineType? = null,
    var serviceType: EnumServiceType? = null,
    var machineId: UUID? = null,
    var customerId: UUID? = null,
): BaseFilterParams(
    orderBy = "Date used",
    sortDirection = EnumSortDirection.DESC
), Parcelable