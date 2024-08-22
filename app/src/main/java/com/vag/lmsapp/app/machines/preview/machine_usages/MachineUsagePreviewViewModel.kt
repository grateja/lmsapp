package com.vag.lmsapp.app.machines.preview.machine_usages

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.app.printer.PrinterPreviewActivity
import com.vag.lmsapp.model.PrinterItem
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toShort
import java.util.ArrayList

class MachineUsagePreviewViewModel: ViewModel() {
    private val _dataState = MutableLiveData<DataState<List<PrinterItem>>>()
    val dataState: LiveData<DataState<List<PrinterItem>>> = _dataState

    private val _machineUsage = MutableLiveData<EntityMachineUsageDetails>()
    val machineUsageDetails: LiveData<EntityMachineUsageDetails> = _machineUsage

    fun setMachineUsage(machineUsage: EntityMachineUsageDetails) {
        _machineUsage.value = machineUsage
    }

    fun initiatePrint() {
        _machineUsage.value?.let {
            val bundle = Bundle().apply {
                putParcelableArrayList(PrinterPreviewActivity.PRINTER_ITEMS_EXTRA, ArrayList(it.printItems()))
            }
            _dataState.value = DataState.OpenActivity(PrinterPreviewActivity::class.java, bundle)
        }
//        val items = mutableListOf<PrinterItem>()
//        val machineUsage = _machineUsage.value ?: return
//
//        items.add(PrinterItem.TextCenter("DATE: ${machineUsage.activated.toShort()}"))
//        items.add(PrinterItem.HeaderDoubleCenter("JO#: ${machineUsage.jobOrderNumber}"))
//        items.add(PrinterItem.HeaderDoubleCenter(machineUsage.serviceName))
//        items.add(PrinterItem.TextCenterTall(machineUsage.customerName))
//
//        _dataState.value = DataState.Submit(items)
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}