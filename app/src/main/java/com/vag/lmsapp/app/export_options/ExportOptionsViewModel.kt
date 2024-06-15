package com.vag.lmsapp.app.export_options

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.repository.ExportRepository
import com.vag.lmsapp.util.toShort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExportOptionsViewModel

@Inject constructor(
    private val exportRepository: ExportRepository
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _dateFilter = MutableLiveData(DateFilter())
    val dateFilter: LiveData<DateFilter> = _dateFilter

    val includeJobOrders = MutableLiveData(true)
    val includeServices = MutableLiveData(true)
    val includeProducts = MutableLiveData(true)
    val includeExtras = MutableLiveData(true)
    val includeMachineUsages = MutableLiveData(true)
    val includeDeliveryCharges = MutableLiveData(true)
    val includeExpenses = MutableLiveData(true)

    val jobOrdersCount = _dateFilter.switchMap { exportRepository.jobOrdersCount(it) }
    val jobOrdersServicesCount = _dateFilter.switchMap { exportRepository.jobOrderServicesCount(it) }
    val jobOrdersProductsCount = _dateFilter.switchMap { exportRepository.jobOrderProductsCount(it) }
    val jobOrdersExtrasCount = _dateFilter.switchMap { exportRepository.jobOrderExtrasCount(it) }
    val machineUsagesCount = _dateFilter.switchMap { exportRepository.machineUsagesCount(it) }
    val deliveryChargesCount = _dateFilter.switchMap { exportRepository.deliveryChargesCount(it) }
    val expensesCount = _dateFilter.switchMap { exportRepository.expensesCount(it) }

    fun setDateFilter(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }

    fun setDateFrom(localDate: LocalDate) {
        _dateFilter.value = _dateFilter.value?.copy(
            dateFrom = localDate
        )
    }

    fun setDateTo(localDate: LocalDate) {
        _dateFilter.value = _dateFilter.value?.copy(
            dateTo = localDate
        )
    }

    fun browseDateFrom() {
        val dateFrom = _dateFilter.value?.dateFrom ?: LocalDate.now()
        _navigationState.value = NavigationState.BrowseDateFrom(dateFrom)
    }

    fun browseDateTo() {
        val dateTo = _dateFilter.value?.dateTo ?: LocalDate.now()
        _navigationState.value = NavigationState.BrowseDateTo(dateTo)
    }

    fun showCreateDialog() {
        val filename = _dateFilter.value?.toString() ?: "file"
        _navigationState.value = NavigationState.ShowCreateDialog(filename)
    }

    fun prepareWorkBook(uri: Uri) {
        viewModelScope.launch {
            val dateFilter = _dateFilter.value ?: return@launch
            val workbook = XSSFWorkbook()

            if(includeJobOrders.value == true) {
                val jobOrders = exportRepository.jobOrders(dateFilter)
                val jobOrderSheet = workbook.createSheet("Job Orders").apply {
                    createHeader(this, arrayOf(
                        "CREATED", "JO#", "CUSTOMER", "SUBTOTAL", "DISCOUNT", "DISCOUNTED AMOUNT",
                        "DATE PAID", "PAYMENT METHOD", "PMT. RECEIVED BY"))
                }

                jobOrders.forEachIndexed { index, entityJobOrderWithItems ->
                    jobOrderSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(entityJobOrderWithItems.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(entityJobOrderWithItems.jobOrderNumber)
                        }
                        createCell(2).apply {
                            setCellValue(entityJobOrderWithItems.customerName)
                        }
                        createCell(3).apply {
                            setCellValue(entityJobOrderWithItems.subtotal)
                        }
                        createCell(4).apply {
                            setCellValue(entityJobOrderWithItems.discountStr)
                        }
                        createCell(5).apply {
                            setCellValue(entityJobOrderWithItems.discountedAmount)
                        }
                        createCell(6).apply {
                            setCellValue(entityJobOrderWithItems.datePaid?.toShort())
                        }
                        createCell(7).apply {
                            setCellValue(entityJobOrderWithItems.paymentMethod)
                        }
                        createCell(8).apply {
                            setCellValue(entityJobOrderWithItems.receivedBy)
                        }
                    }
                }
            }

            if(includeServices.value == true) {
                val jobOrderServices = exportRepository.jobOrderServices(dateFilter)
                val jobOrderServicesSheet = workbook.createSheet("Services").apply {
                    createHeader(this, arrayOf(
                        "CREATED", "JO#", "CUSTOMER", "SERVICE NAME", "MACHINE TYPE", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"
                    ))
                }
                jobOrderServices.forEachIndexed { index, exportDataJobOrderService ->
                    jobOrderServicesSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataJobOrderService.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataJobOrderService.jobOrderNumber)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataJobOrderService.customerName)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataJobOrderService.serviceName)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataJobOrderService.machineType.toString())
                        }
                        createCell(5).apply {
                            setCellValue(exportDataJobOrderService.quantity.toString())
                        }
                        createCell(6).apply {
                            setCellValue(exportDataJobOrderService.price)
                        }
                        createCell(7).apply {
                            cellFormula = "F${index + 2}*G${index + 2}"
                        }
                    }
                }
            }

            if(includeProducts.value == true) {
                val jobOrderProductSheet = workbook.createSheet("Products").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "PRODUCT NAME",
                        "QUANTITY", "DESCRIPTION", "UNIT PRICE", "TOTAL PRICE"))
                }
                val jobOrderProducts = exportRepository.jobOrderProducts(dateFilter)
                jobOrderProducts.forEachIndexed { index, exportDataJobOrderProduct ->
                    jobOrderProductSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataJobOrderProduct.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataJobOrderProduct.jobOrderNumber)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataJobOrderProduct.customerName)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataJobOrderProduct.productName)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataJobOrderProduct.quantity.toString())
                        }
                        createCell(5).apply {
                            setCellValue(exportDataJobOrderProduct.measureUnit.value)
                        }
                        createCell(6).apply {
                            setCellValue(exportDataJobOrderProduct.price)
                        }
                        createCell(7).apply {
                            cellFormula = "G${index + 2}*E${index + 2}"
                        }
                    }
                }
            }

            if(includeExtras.value == true) {
                val jobOrderExtrasSheet = workbook.createSheet("Extras").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "EXTRAS NAME",
                        "CATEGORY", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"))
                }
                val jobOrderExtras = exportRepository.jobOrderExtras(dateFilter)
                jobOrderExtras.forEachIndexed { index, exportDataJobOrderExtras ->
                    jobOrderExtrasSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataJobOrderExtras.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataJobOrderExtras.jobOrderNumber)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataJobOrderExtras.customerName)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataJobOrderExtras.extrasName)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataJobOrderExtras.category)
                        }
                        createCell(5).apply {
                            setCellValue(exportDataJobOrderExtras.quantity.toString())
                        }
                        createCell(6).apply {
                            setCellValue(exportDataJobOrderExtras.price)
                        }
                        createCell(7).apply {
                            cellFormula = "F${index + 2}*G${index + 2}"
                        }
                    }
                }
            }

            if(includeDeliveryCharges.value == true) {
                val deliveryChargeSheet = workbook.createSheet("Delivery Charges").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "VEHICLE",
                        "DELIVERY OPTION", "DISTANCE", "PRICE"))
                }
                val deliveryCharges = exportRepository.deliveryCharges(dateFilter)
                deliveryCharges.forEachIndexed { index, exportDataDeliveryCharges ->
                    deliveryChargeSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataDeliveryCharges.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataDeliveryCharges.jobOrderNumber)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataDeliveryCharges.customerName)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataDeliveryCharges.vehicle.value)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataDeliveryCharges.deliveryOption.value)
                        }
                        createCell(5).apply {
                            setCellValue(exportDataDeliveryCharges.distance)
                        }
                        createCell(6).apply {
                            setCellValue(exportDataDeliveryCharges.price)
                        }
                    }
                }
            }

            if(includeMachineUsages.value == true) {
                val machineUsageSheet = workbook.createSheet("Machine Usage").apply {
                    createHeader(this, arrayOf("TIME ACTIVATED", "MACHINE", "SERVICE NAME",
                        "MINUTES", "CUSTOMER", "JO#", "CREATED"))
                }
                val machineUsages = exportRepository.machineUsages(dateFilter)
                machineUsages.forEachIndexed { index, exportDataMachineUsage ->
                    machineUsageSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataMachineUsage.activated.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataMachineUsage.toString())
                        }
                        createCell(2).apply {
                            setCellValue(exportDataMachineUsage.serviceName)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataMachineUsage.minutes.toString())
                        }
                        createCell(4).apply {
                            setCellValue(exportDataMachineUsage.customerName)
                        }
                        createCell(5).apply {
                            setCellValue(exportDataMachineUsage.jobOrderNumber)
                        }
                        createCell(6).apply {
                            setCellValue(exportDataMachineUsage.createdAt.toShort())
                        }
                    }
                }
            }

            if(includeExpenses.value == true) {
                val expensesSheet = workbook.createSheet("Expenses").apply {
                    createHeader(this, arrayOf("CREATED", "REMARKS", "AMOUNT",
                        "ADDED BY", "TAG"))
                }
                val expenses = exportRepository.expenses(dateFilter)
                expenses.forEachIndexed { index, exportDataExpenses ->
                    expensesSheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataExpenses.createdAt.toShort())
                        }
                        createCell(1).apply {
                            setCellValue(exportDataExpenses.remarks)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataExpenses.amount)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataExpenses.createdBy)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataExpenses.tag)
                        }
                    }
                }
            }

            _navigationState.value = NavigationState.PrepareWorkbook(workbook, uri)
        }
    }

    private fun createHeader(sheet: XSSFSheet, headers: Array<String>): XSSFRow {
        return sheet.createRow(0).apply {
            headers.forEachIndexed { index, header ->
                createCell(index).apply {
                    setCellValue(header)
                }
            }
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class BrowseDateFrom(val localDate: LocalDate): NavigationState()
        data class BrowseDateTo(val localDate: LocalDate): NavigationState()
        data class ShowCreateDialog(val filename: String): NavigationState()
        data class PrepareWorkbook(val workbook: XSSFWorkbook, val uri: Uri): NavigationState()
    }
}