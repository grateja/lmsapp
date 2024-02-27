package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class MachineActivationQueues(
    val machineId: UUID,
    val jobOrderServiceId: UUID?,
    val customerId: UUID?,
//    val customerName: String,
//    val serviceName: String,
//    val machineName: String,
    var status: MachineConnectionStatus? = null,
    var message: String? = null,
): Parcelable {
    fun connecting() : Boolean {
        return status == MachineConnectionStatus.CONNECTING
    }

    fun canActivate() : Boolean {
        return status == MachineConnectionStatus.FAILED || status  == MachineConnectionStatus.READY
    }

    fun isInconsistent() : Boolean {
        return status == MachineConnectionStatus.DATA_INCONSISTENT
    }

    fun isSuccess() : Boolean {
        return status == MachineConnectionStatus.SUCCESS
    }
}