package com.vag.lmsapp.app.machines

import android.os.Parcelable
import com.vag.lmsapp.model.EnumMachineType
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MachineMinimal(
    val id: UUID,
    val name: String,
    val machineType: EnumMachineType
) : Parcelable