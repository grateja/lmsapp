package com.vag.lmsapp.model

data class MachineTypeFilter(
    val machineType: EnumMachineType?,
    val serviceType: EnumServiceType?
) {
    override fun toString(): String {
        return "${machineType?.value} ${serviceType?.value}er"
    }
}
