package com.vag.lmsapp.app.remote.print

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.PrinterItem
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.util.toShort
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RemoteActivationPrintViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
): ViewModel() {
    private val _machineUsageId = MutableLiveData<UUID>()
    val machineUsage = _machineUsageId.switchMap { machineRepository.getMachineUsageAsLiveData(it) }

//    val text = MediatorLiveData<String>().apply {
//        addSource(machineUsage) {usage ->
//            val items = mutableListOf<PrinterItem>()
//            if(usage != null) {
//                items.add(PrinterItem.TextCenter("DATE: ${usage.activated.toShort()}"))
//                items.add(PrinterItem.HeaderDoubleCenter("JO#: ${usage.jobOrderNumber}"))
//                items.add(PrinterItem.HeaderDoubleCenter(usage.serviceLabel()))
//                items.add(PrinterItem.TextCenterTall(usage.customerName))
//            }
//        }
//    }

    fun setMachineUsageId(machineUsageId: UUID) {
        _machineUsageId.value = machineUsageId
    }
}