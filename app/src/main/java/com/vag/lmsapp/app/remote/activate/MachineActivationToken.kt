package com.vag.lmsapp.app.remote.activate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class MachineActivationToken(
    var machineId: UUID,
    var joServiceId: UUID,
    var customerId: UUID,
) : Parcelable