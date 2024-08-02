package com.vag.lmsapp.app.export_options

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.squareup.moshi.Moshi
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.export_options.data.ProgressReport
import com.vag.lmsapp.app.export_options.data.Selections
import com.vag.lmsapp.room.repository.ExportRepository
//import com.vag.lmsapp.services.ExcelExportService.Companion.FILENAME_JSON
import com.vag.lmsapp.services.ExcelExportService.Companion.FILENAME_XLSX
import com.vag.lmsapp.settings.OperationSettingsRepository
import com.vag.lmsapp.util.file.FileExistenceLiveData
import com.vag.lmsapp.util.setMomentAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExportOptionsViewModel

@Inject constructor(
    private val exportRepository: ExportRepository,
    private val operationSettingsRepository: OperationSettingsRepository,
    @ApplicationContext context: Context
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _dateFilter = MutableLiveData(DateFilter())
    val dateFilter: LiveData<DateFilter> = _dateFilter

    private val _progressReport = MutableLiveData<ProgressReport?>()
    val progressReport: LiveData<ProgressReport?> = _progressReport

    private var _selections = arrayListOf<Selections>()

    val file = FileExistenceLiveData(context.filesDir.toString(), FILENAME_XLSX)
    val oldDateFilter = operationSettingsRepository.dateFilter
//    val json = FileExistenceLiveData(context.filesDir.toString(), FILENAME_JSON)

    val requireRefresh = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = _dateFilter.value != oldDateFilter.value
        }
        addSource(_dateFilter) {update()}
        addSource(oldDateFilter) {update()}
    }

    val exportString = MediatorLiveData<String>().apply {
        fun update() {
            val progress = _progressReport.value
            val file = file.value
            value = if(progress != null) {
                "$progress.\n Tap to cancel."
            } else {
                if(requireRefresh.value == true) {
                    "Create file(${_dateFilter.value.toString()})"
                } else if(file != null && file.exists()) {
                    "File created " + Instant.ofEpochMilli(file.lastModified()).setMomentAgo() +
                            ".\n Tap again to update or choose options below."
                } else {
                    "Create file"
                }
            }
        }
        addSource(_progressReport) {update()}
        addSource(file) {update()}
        addSource(requireRefresh) {update()}
    }

//
//    val includeJobOrders = MutableLiveData(true)
//    val includeServices = MutableLiveData(true)
//    val includeProducts = MutableLiveData(true)
//    val includeExtras = MutableLiveData(true)
//    val includeMachineUsages = MutableLiveData(true)
//    val includeDeliveryCharges = MutableLiveData(true)
//    val includeExpenses = MutableLiveData(true)
//    val includeCustomers = MutableLiveData(true)
//    val includeUnpaidJobOrders = MutableLiveData(true)

    val jobOrdersCount = _dateFilter.switchMap { exportRepository.jobOrdersCount(it) }
    val jobOrdersServicesCount = _dateFilter.switchMap { exportRepository.jobOrderServicesCount(it) }
    val jobOrdersProductsCount = _dateFilter.switchMap { exportRepository.jobOrderProductsCount(it) }
    val jobOrdersExtrasCount = _dateFilter.switchMap { exportRepository.jobOrderExtrasCount(it) }
    val machineUsagesCount = _dateFilter.switchMap { exportRepository.machineUsagesCount(it) }
    val deliveryChargesCount = _dateFilter.switchMap { exportRepository.deliveryChargesCount(it) }
    val expensesCount = _dateFilter.switchMap { exportRepository.expensesCount(it) }
    val customersCount = _dateFilter.switchMap { exportRepository.customersCount(it) }
    val unpaidJobOrdersCount = exportRepository.unpaidJobOrdersCount()

    fun setDateFilter(dateFilter: DateFilter) {
        println("set date filter")
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
        val filename = oldDateFilter.value?.toString() ?: "file"
        _navigationState.value = NavigationState.ShowCreateDialog(filename)
    }

//    fun invalid(): Boolean {
//        val invalid =
//            (includeExtras.value == false || jobOrdersExtrasCount.value == 0)
//            && (includeExpenses.value == false || expensesCount.value == 0)
//            && (includeProducts.value == false || jobOrdersProductsCount.value == 0)
//            && (includeServices.value == false || jobOrdersServicesCount.value == 0)
//            && (includeMachineUsages.value == false || machineUsagesCount.value == 0)
//            && (includeJobOrders.value == false || jobOrdersCount.value == 0)
//            && (includeDeliveryCharges.value == false || deliveryChargesCount.value == 0)
//            && (includeUnpaidJobOrders.value == false || unpaidJobOrdersCount.value == 0)
//        if(invalid) {
//            _navigationState.value = NavigationState.Invalidate("Nothing to export")
//        }
//        return invalid
//    }



//    private suspend fun prepareWorkBook(): XSSFWorkbook? {
//        val dateFilter = _dateFilter.value ?: return null
//        val workbook = XSSFWorkbook()
//        var hasItem = false
//
//        if(includeJobOrders.value == true && jobOrdersCount.value.greaterThan(0)) {
//            hasItem = true
//            val jobOrders = exportRepository.jobOrders(dateFilter)
//            val jobOrderSheet = workbook.createSheet("Job Orders").apply {
//                createHeader(this, arrayOf(
//                    "CREATED", "JO#", "CUSTOMER", "SUBTOTAL", "DISCOUNT", "DISCOUNTED AMOUNT",
//                    "DATE PAID", "PAYMENT METHOD", "PMT. RECEIVED BY"))
//            }
//
//            jobOrders.forEachIndexed { index, entityJobOrderWithItems ->
//                jobOrderSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(entityJobOrderWithItems.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(entityJobOrderWithItems.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(entityJobOrderWithItems.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(entityJobOrderWithItems.subtotal)
//                    }
//                    createCell(4).apply {
//                        setCellValue(entityJobOrderWithItems.discountStr)
//                    }
//                    createCell(5).apply {
//                        setCellValue(entityJobOrderWithItems.discountedAmount)
//                    }
//                    createCell(6).apply {
//                        setCellValue(entityJobOrderWithItems.datePaid?.toShort())
//                    }
//                    createCell(7).apply {
//                        setCellValue(entityJobOrderWithItems.paymentMethod)
//                    }
//                    createCell(8).apply {
//                        setCellValue(entityJobOrderWithItems.receivedBy)
//                    }
//                }
//            }
//        }
//
//        if(includeUnpaidJobOrders.value == true && unpaidJobOrdersCount.value.greaterThan(0)) {
//            hasItem = true
//            val jobOrders = exportRepository.unpaidJobOrders()
//            val jobOrderSheet = workbook.createSheet("Unpaid Job Orders").apply {
//                createHeader(this, arrayOf(
//                    "CREATED", "JO#", "CUSTOMER", "SUBTOTAL", "DISCOUNT", "DISCOUNTED AMOUNT"))
//            }
//
//            jobOrders.forEachIndexed { index, entityJobOrderWithItems ->
//                jobOrderSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(entityJobOrderWithItems.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(entityJobOrderWithItems.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(entityJobOrderWithItems.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(entityJobOrderWithItems.subtotal)
//                    }
//                    createCell(4).apply {
//                        setCellValue(entityJobOrderWithItems.discountStr)
//                    }
//                    createCell(5).apply {
//                        setCellValue(entityJobOrderWithItems.discountedAmount)
//                    }
//                    createCell(6).apply {
//                        setCellValue(entityJobOrderWithItems.datePaid?.toShort())
//                    }
//                    createCell(7).apply {
//                        setCellValue(entityJobOrderWithItems.paymentMethod)
//                    }
//                    createCell(8).apply {
//                        setCellValue(entityJobOrderWithItems.receivedBy)
//                    }
//                }
//            }
//        }
//
//        if(includeServices.value == true && jobOrdersServicesCount.value.greaterThan(0)) {
//            hasItem = true
//            val jobOrderServices = exportRepository.jobOrderServices(dateFilter)
//            val jobOrderServicesSheet = workbook.createSheet("Services").apply {
//                createHeader(this, arrayOf(
//                    "CREATED", "JO#", "CUSTOMER", "SERVICE NAME", "MACHINE TYPE", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"
//                ))
//            }
//            jobOrderServices.forEachIndexed { index, exportDataJobOrderService ->
//                jobOrderServicesSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataJobOrderService.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataJobOrderService.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataJobOrderService.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataJobOrderService.serviceName)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataJobOrderService.machineType.toString())
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataJobOrderService.quantity.toString())
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataJobOrderService.discountedPrice)
//                    }
//                    createCell(7).apply {
//                        cellFormula = "F${index + 2}*G${index + 2}"
//                    }
//                }
//            }
//        }
//
//        if(includeProducts.value == true && jobOrdersProductsCount.value.greaterThan(0)) {
//            hasItem = true
//            val jobOrderProductSheet = workbook.createSheet("Products").apply {
//                createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "PRODUCT NAME",
//                    "QUANTITY", "DESCRIPTION", "UNIT PRICE", "TOTAL PRICE"))
//            }
//            val jobOrderProducts = exportRepository.jobOrderProducts(dateFilter)
//            jobOrderProducts.forEachIndexed { index, exportDataJobOrderProduct ->
//                jobOrderProductSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataJobOrderProduct.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataJobOrderProduct.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataJobOrderProduct.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataJobOrderProduct.productName)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataJobOrderProduct.quantity.toString())
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataJobOrderProduct.measureUnit.value)
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataJobOrderProduct.discountedPrice)
//                    }
//                    createCell(7).apply {
//                        cellFormula = "G${index + 2}*E${index + 2}"
//                    }
//                }
//            }
//        }
//
//        if(includeExtras.value == true && jobOrdersExtrasCount.value.greaterThan(0)) {
//            hasItem = true
//            val jobOrderExtrasSheet = workbook.createSheet("Extras").apply {
//                createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "EXTRAS NAME",
//                    "CATEGORY", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"))
//            }
//            val jobOrderExtras = exportRepository.jobOrderExtras(dateFilter)
//            jobOrderExtras.forEachIndexed { index, exportDataJobOrderExtras ->
//                jobOrderExtrasSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataJobOrderExtras.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataJobOrderExtras.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataJobOrderExtras.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataJobOrderExtras.extrasName)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataJobOrderExtras.category)
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataJobOrderExtras.quantity.toString())
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataJobOrderExtras.discountedPrice)
//                    }
//                    createCell(7).apply {
//                        cellFormula = "F${index + 2}*G${index + 2}"
//                    }
//                }
//            }
//        }
//
//        if(includeDeliveryCharges.value == true && deliveryChargesCount.value.greaterThan(0)) {
//            hasItem = true
//            val deliveryChargeSheet = workbook.createSheet("Delivery Charges").apply {
//                createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "VEHICLE",
//                    "DELIVERY OPTION", "DISTANCE", "PRICE"))
//            }
//            val deliveryCharges = exportRepository.deliveryCharges(dateFilter)
//            deliveryCharges.forEachIndexed { index, exportDataDeliveryCharges ->
//                deliveryChargeSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataDeliveryCharges.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataDeliveryCharges.jobOrderNumber)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataDeliveryCharges.customerName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataDeliveryCharges.vehicle.value)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataDeliveryCharges.deliveryOption.value)
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataDeliveryCharges.distance)
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataDeliveryCharges.discountedPrice)
//                    }
//                }
//            }
//        }
//
//        if(includeMachineUsages.value == true && machineUsagesCount.value.greaterThan(0)) {
//            hasItem = true
//            val machineUsageSheet = workbook.createSheet("Machine Usage").apply {
//                createHeader(this, arrayOf("TIME ACTIVATED", "MACHINE", "SERVICE NAME",
//                    "MINUTES", "CUSTOMER", "JO#", "CREATED"))
//            }
//            val machineUsages = exportRepository.machineUsages(dateFilter)
//            machineUsages.forEachIndexed { index, exportDataMachineUsage ->
//                machineUsageSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataMachineUsage.activated.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataMachineUsage.toString())
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataMachineUsage.serviceName)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataMachineUsage.minutes.toString())
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataMachineUsage.customerName)
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataMachineUsage.jobOrderNumber)
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataMachineUsage.createdAt.toShort())
//                    }
//                }
//            }
//        }
//
//        if(includeExpenses.value == true && expensesCount.value.greaterThan(0)) {
//            hasItem = true
//            val expensesSheet = workbook.createSheet("Expenses").apply {
//                createHeader(this, arrayOf("CREATED", "REMARKS", "AMOUNT",
//                    "ADDED BY", "TAG"))
//            }
//            val expenses = exportRepository.expenses(dateFilter)
//            expenses.forEachIndexed { index, exportDataExpenses ->
//                expensesSheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataExpenses.createdAt.toShort())
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataExpenses.remarks)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataExpenses.amount)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataExpenses.createdBy)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataExpenses.tag)
//                    }
//                }
//            }
//        }
//
//        if(includeCustomers.value == true && customersCount.value.greaterThan(0)) {
//            hasItem = true
//            val customers = exportRepository.customers(dateFilter)
//            val customersWorksheet = workbook.createSheet("Customers").apply {
//                createHeader(this, arrayOf(
//                    "CRN", "NAME", "CONTACT NUMBER", "ADDRESS", "EMAIL", "REMARKS", "TOTAL JOB ORDERS",
//                    "NUMBER OF WASHES", "NUMBER OF DRIES", "FIRST JO", "LAST JO"
//                ))
//            }
//
//            customers.forEachIndexed { index, exportDataNewCustomers ->
//                customersWorksheet.createRow(index + 1).apply {
//                    createCell(0).apply {
//                        setCellValue(exportDataNewCustomers.crn)
//                    }
//                    createCell(1).apply {
//                        setCellValue(exportDataNewCustomers.name)
//                    }
//                    createCell(2).apply {
//                        setCellValue(exportDataNewCustomers.contactNumber)
//                    }
//                    createCell(3).apply {
//                        setCellValue(exportDataNewCustomers.address)
//                    }
//                    createCell(4).apply {
//                        setCellValue(exportDataNewCustomers.email)
//                    }
//                    createCell(5).apply {
//                        setCellValue(exportDataNewCustomers.remarks)
//                    }
//                    createCell(6).apply {
//                        setCellValue(exportDataNewCustomers.totalJobOrders.toString())
//                    }
//                    createCell(7).apply {
//                        setCellValue(exportDataNewCustomers.totalWashes.toString())
//                    }
//                    createCell(8).apply {
//                        setCellValue(exportDataNewCustomers.totalDries.toString())
//                    }
//                    createCell(9).apply {
//                        setCellValue(exportDataNewCustomers.firstJo?.toShort())
//                    }
//                    createCell(10).apply {
//                        setCellValue(exportDataNewCustomers.lastJo?.toShort())
//                    }
//                }
//            }
//        }
//
//        return if(hasItem) workbook else null
//    }

    fun createFile() {
        if(_selections.size == 0) {
            _navigationState.value = NavigationState.Invalidate("Please select at least 1 item!")
            return
        }
        val dateFilter = _dateFilter.value ?: DateFilter()
        val selections = ArrayList(_selections.map { it.key })
        _navigationState.value = NavigationState.StartExportService(dateFilter, selections)
    }

//    fun openPreview() {
//        viewModelScope.launch {
//            if(!invalid()) {
//                prepareWorkBook()?.let {
//                    val filename = _dateFilter.value.toString()
//                    _navigationState.value = NavigationState.OpenWorkbook(it, filename)
//                }
//            }
//        }
//    }

//    fun prepareSave(uri: Uri) {
//        viewModelScope.launch {
//            if(!invalid()) {
//                prepareWorkBook()?.let {
//                    _navigationState.value = NavigationState.SaveWorkbook(it, uri)
//                }
//            }
//        }
//    }

//    fun prepareSend() {
//        viewModelScope.launch {
//            if(!invalid()) {
//                prepareWorkBook()?.let {
//                    val filename = _dateFilter.value.toString()
//                    _navigationState.value = NavigationState.SendWorkbook(it, filename)
//                }
//            }
//        }
//    }

//    private fun createHeader(sheet: XSSFSheet, headers: Array<String>): XSSFRow {
//        return sheet.createRow(0).apply {
//            headers.forEachIndexed { index, header ->
//                createCell(index).apply {
//                    setCellValue(header)
//                }
//            }
//        }
//    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun setSelection(selections: ArrayList<Selections>) {
        _selections = selections
    }

    fun setProgress(progress: ProgressReport?) {
        _progressReport.value = progress
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class BrowseDateFrom(val localDate: LocalDate): NavigationState()
        data class BrowseDateTo(val localDate: LocalDate): NavigationState()
        data class ShowCreateDialog(val filename: String): NavigationState()
//        data class SaveWorkbook(val workbook: XSSFWorkbook, val uri: Uri): NavigationState()
//        data class SendWorkbook(val workbook: XSSFWorkbook, val filename: String): NavigationState()
//        data class OpenWorkbook(val workbook: XSSFWorkbook, val filename: String): NavigationState()
        data class StartExportService(val dateFilter: DateFilter, val selections: ArrayList<String>): NavigationState()
        data class Invalidate(val message: String): NavigationState()
    }
}