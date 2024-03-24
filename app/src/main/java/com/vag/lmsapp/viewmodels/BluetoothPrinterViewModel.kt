package com.vag.lmsapp.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.util.PrinterHelper
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BluetoothPrinterViewModel
@Inject
constructor(
): ViewModel() {
    private lateinit var printerHelper: PrinterHelper
    private val connection: BluetoothConnection? = null
    val printer = MutableLiveData<EscPosPrinter>()
//    val adapter = Adapter<BluetoothConnection>(R.layout.recycler_item_printer).apply {
//        allowSelection = true
//    }

    fun initializeBluetooth(activity: AppCompatActivity) {
        printerHelper = PrinterHelper(activity)
    }

    fun selectPrinter() {
//        viewModelScope.launch {
//            withContext(Dispatchers.Default) {
////                val connection = BluetoothConnection(adapter.getSelectedItem()?.getItem()?.device)
//                appPreferenceRepository.printerSettings().let {
//                    try {
//                        println("Setting printer")
//                        val _printer = EscPosPrinter(connection, it.dpi, it.width, it.character)
//                        println("printer cached")
//                        printer.postValue(_printer)
//                        println("Printer set")
//                    } catch (e: Exception) {
//                        println(e.message)
//                    }
//                }
//            }
//        }
    }

    fun loadPrinters() {
        printerHelper.printers()?.let { _list ->
//            adapter.setData(_list.map {
//                RecyclerItem(it).apply {
//                    selected = appPreferenceRepository.printer().address == it.device.address
//                }
//            })
        }
        printerHelper.searchNearByDevice()
    }
}