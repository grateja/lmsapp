package com.vag.lmsapp.app.remote.panel

//import com.csi.palabakosys.app.remote.shared_ui.MachineStatus
import com.vag.lmsapp.room.entities.EntityMachine

data class MachineTile(
    val entityMachine: EntityMachine,
) {
    var connecting = false
}