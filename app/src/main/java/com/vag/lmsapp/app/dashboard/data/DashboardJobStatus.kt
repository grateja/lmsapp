package com.vag.lmsapp.app.dashboard.data

import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.room.entities.EntityJobStatus

data class DashboardJobStatus(
    val jobStatus: EntityJobStatus?,
    val runningMachines: List<MachineListItem>?
)