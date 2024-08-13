package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.export_options.ExportOptionsActivity
import com.vag.lmsapp.app.export_options.data.ProgressReport
import com.vag.lmsapp.room.repository.ExportRepository
import com.vag.lmsapp.settings.OperationSettingsRepository
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.toShort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject

@AndroidEntryPoint
class ExcelExportService: Service() {
    @Inject
    lateinit var exportRepository: ExportRepository

    @Inject
    lateinit var operationSettingsRepository: OperationSettingsRepository

    companion object {
        private const val CHANNEL_ID = "export_channel_id"
        private const val NOTIFICATION_ID = 86209

        const val FILENAME_XLSX = "excel-report.xlsx"
//        const val FILENAME_JSON = "date-filter-meta-data.json"

        const val INCLUSIONS = "inclusions"
        const val RUNNING = "running"

        const val INCLUDES_JOB_ORDER = "includes_job_order"
        const val INCLUDES_NEW_CUSTOMERS = "includes_new_customers"
        const val INCLUDES_WASH_DRY_SERVICES = "includes_wash_dry_services"
        const val INCLUDES_PRODUCTS_CHEMICALS = "includes_products_chemicals"
        const val INCLUDES_EXTRAS = "includes_extras"
        const val INCLUDES_MACHINE_USAGES = "includes_machine_usages"
        const val INCLUDES_DELIVERY_CHARGES = "includes_delivery_charges"
        const val INCLUDES_EXPENSES = "includes_expenses"
        const val INCLUDES_UNPAID_JOB_ORDERS = "includes_unpaid_job_orders"

        const val ACTION_START = "action_start"
        const val ACTION_CANCEL = "action_cancel"
        const val ACTION_FILE_CREATED = "action_file_created"
        const val ACTION_PENDING = "action_pending"
        const val ACTION_CHECK = "action_check"
        const val ACTION_PROGRESS = "action_progress"

        const val PROGRESS_EXTRA = "progress_string_extra"

        var running = false

        fun check(context: Context) {
            val intent = Intent(context, ExcelExportService::class.java).apply {
                action = ACTION_CHECK
            }
            context.startForegroundService(intent)
        }
    }

    private var _dateFilter: DateFilter? = null
    private var _inclusions: ArrayList<String>? = null

    private var currentIndex: Int = 0
    private var currentCount: Int = 0
    private var currentEntity: String = ""
    private var stopFlag = false

    override fun onBind(p0: Intent?): IBinder? = null

    private val notificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createChannel() =
        NotificationChannel(
            CHANNEL_ID,
            "Excel export",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Generating large excel report requires background running service"
            setSound(null, null)
        }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification("Export started"))
    }


    private fun getNotification(message: String?, progress: Int = 0, count: Int = 0): Notification {
        val notificationIntent = Intent(this, ExportOptionsActivity::class.java).apply {
            putExtra(DATE_RANGE_FILTER, _dateFilter)
            putStringArrayListExtra(INCLUSIONS, _inclusions)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(this.getString(R.string.app_name))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            .setContentIntent(pendingIntent)

        if(count > 0) {
            builder.setProgress(count, progress, false)
        }

        notificationManager.createNotificationChannel(createChannel())
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(running && intent?.action == ACTION_CHECK) {
            setBroadcast(ACTION_PENDING)
        } else if(!running && intent?.action == ACTION_START) {
            _dateFilter = intent.getParcelableExtra(DATE_RANGE_FILTER)
            _inclusions = intent.getStringArrayListExtra(INCLUSIONS)
            start()
        } else if(intent?.action == ACTION_CANCEL) {
            cancel()
        }

        return START_NOT_STICKY
    }

    private fun setBroadcast(action: String?) {
        val pending = Intent(action).apply {
            putExtra(RUNNING, running)
            putExtra(DATE_RANGE_FILTER, _dateFilter)
            putStringArrayListExtra(INCLUSIONS, _inclusions)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(pending)
    }

    private fun cancel() {
        running = false
        val pending = Intent(ACTION_CANCEL)
        LocalBroadcastManager.getInstance(this).sendBroadcast(pending)
        stopFlag = true
        stopSelf()
    }

    private fun prepareProgress() {
        val pending = Intent(ACTION_PROGRESS).apply {
            putExtra(PROGRESS_EXTRA, ProgressReport(currentEntity, currentIndex, currentCount))
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(pending)
    }

    private fun start() {
        Thread {
            runBlocking {
                if(!running) {
                    running = true
                    val dateFilter = _dateFilter
                    val inclusions = _inclusions

                    if(dateFilter == null || inclusions == null) {
                        running = false
                        return@runBlocking
                    }

                    File(filesDir, FILENAME_XLSX).delete()
//                    File(filesDir, FILENAME_JSON).delete()

                    setBroadcast(ACTION_START)

                    prepareWorkBook(dateFilter, inclusions).let {
                        if(it == null) {
                            return@runBlocking
                        }
                        FileOutputStream(File(filesDir, FILENAME_XLSX)).use { fos ->
                            OutputStreamWriter(fos).use { writer ->
                                it.write(fos)
                            }
                        }
                    }

                    operationSettingsRepository.setDateFilter(dateFilter)

//                    FileOutputStream(File(filesDir, FILENAME_JSON)).use { fos ->
//                        OutputStreamWriter(fos).use { writer ->
//                            writer.write(moshi.adapter(DateFilter::class.java).toJson(dateFilter))
//                        }
//                    }

                    startForeground(NOTIFICATION_ID, getNotification("Export done"))
                    setBroadcast(ACTION_FILE_CREATED)

                    running = false
                }
            }
        }.start()
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


    private suspend fun prepareWorkBook(dateFilter: DateFilter, inclusions: ArrayList<String>): XSSFWorkbook? {
        val workbook = XSSFWorkbook()
        var hasItem = false

        if(inclusions.contains(INCLUDES_JOB_ORDER)) {
            exportRepository.jobOrders(dateFilter).takeIf { it.isNotEmpty() }?.let { jobOrders ->
                hasItem = true
                currentCount = jobOrders.size
                currentEntity = "Job orders"
                val jobOrderSheet = workbook.createSheet("Job Orders").apply {
                    createHeader(this, arrayOf(
                        "CREATED", "JO#", "CUSTOMER", "SUBTOTAL", "DISCOUNT", "DISCOUNTED AMOUNT",
                        "DATE PAID", "PAYMENT METHOD", "PMT. RECEIVED BY"))
                }
                jobOrders.forEachIndexed { index, entityJobOrderWithItems ->
                    if(stopFlag) return null

                    currentIndex = index + 1
                    prepareProgress()

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
        }

        if(inclusions.contains(INCLUDES_UNPAID_JOB_ORDERS)) {
            exportRepository.unpaidJobOrders().takeIf { it.isNotEmpty() }?.let { jobOrders ->
                hasItem = true
                currentEntity = "Unpaid Job Orders"
                currentCount = jobOrders.size
                val jobOrderSheet = workbook.createSheet("Unpaid Job Orders").apply {
                    createHeader(this, arrayOf(
                        "CREATED", "JO#", "CUSTOMER", "SUBTOTAL", "DISCOUNT", "DISCOUNTED AMOUNT"))
                }
                jobOrders.forEachIndexed { index, entityJobOrderWithItems ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
        }

        if(inclusions.contains(INCLUDES_WASH_DRY_SERVICES)) {
            exportRepository.jobOrderServices(dateFilter).takeIf { it.isNotEmpty() }?.let {jobOrderServices ->
                hasItem = true
                currentEntity = "Wash and Dry services"
                currentCount = jobOrderServices.size
                val jobOrderServicesSheet = workbook.createSheet("Services").apply {
                    createHeader(this, arrayOf(
                        "CREATED", "JO#", "CUSTOMER", "SERVICE NAME", "MACHINE TYPE", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"
                    ))
                }
                jobOrderServices.forEachIndexed { index, exportDataJobOrderService ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
                            setCellValue(exportDataJobOrderService.discountedPrice)
                        }
                        createCell(7).apply {
                            cellFormula = "F${index + 2}*G${index + 2}"
                        }
                    }
                }
            }
        }

        if(inclusions.contains(INCLUDES_PRODUCTS_CHEMICALS)) {
            exportRepository.jobOrderProducts(dateFilter).takeIf { it.isNotEmpty() }?.let {jobOrderProducts ->
                hasItem = true
                currentEntity = "Products and chemicals"
                currentCount = jobOrderProducts.size
                val jobOrderProductSheet = workbook.createSheet("Products").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "PRODUCT NAME",
                        "QUANTITY", "DESCRIPTION", "UNIT PRICE", "TOTAL PRICE"))
                }
                jobOrderProducts.forEachIndexed { index, exportDataJobOrderProduct ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
                            setCellValue(exportDataJobOrderProduct.discountedPrice)
                        }
                        createCell(7).apply {
                            cellFormula = "G${index + 2}*E${index + 2}"
                        }
                    }
                }
            }
        }

        if(inclusions.contains(INCLUDES_EXTRAS)) {
            exportRepository.jobOrderExtras(dateFilter).takeIf { it.isNotEmpty() }?.let {jobOrderExtras->
                hasItem = true
                currentEntity = "Extra services"
                currentCount = jobOrderExtras.size
                val jobOrderExtrasSheet = workbook.createSheet("Extras").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "EXTRAS NAME",
                        "CATEGORY", "QUANTITY", "UNIT PRICE", "TOTAL PRICE"))
                }
                jobOrderExtras.forEachIndexed { index, exportDataJobOrderExtras ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
                            setCellValue(exportDataJobOrderExtras.discountedPrice)
                        }
                        createCell(7).apply {
                            cellFormula = "F${index + 2}*G${index + 2}"
                        }
                    }
                }
            }
        }

        if(inclusions.contains(INCLUDES_DELIVERY_CHARGES)) {
            exportRepository.deliveryCharges(dateFilter).takeIf { it.isNotEmpty() }?.let {deliveryCharges ->
                hasItem = true
                currentEntity = "Delivery charges"
                currentCount = deliveryCharges.size
                val deliveryChargeSheet = workbook.createSheet("Delivery Charges").apply {
                    createHeader(this, arrayOf("CREATED", "JO#", "CUSTOMER", "VEHICLE",
                        "DELIVERY OPTION", "DISTANCE", "PRICE"))
                }
                deliveryCharges.forEachIndexed { index, exportDataDeliveryCharges ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
                            setCellValue(exportDataDeliveryCharges.discountedPrice)
                        }
                    }
                }
            }
        }

        if(inclusions.contains(INCLUDES_MACHINE_USAGES)) {
            exportRepository.machineUsages(dateFilter).takeIf { it.isNotEmpty() }?.let { machineUsages ->
                hasItem = true
                currentEntity = "Machine usage history"
                currentCount = machineUsages.size
                val machineUsageSheet = workbook.createSheet("Machine Usage").apply {
                    createHeader(this, arrayOf("TIME ACTIVATED", "MACHINE", "SERVICE NAME",
                        "MINUTES", "CUSTOMER", "JO#", "CREATED"))
                }
                machineUsages.forEachIndexed { index, exportDataMachineUsage ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
        }

        if(inclusions.contains(INCLUDES_EXPENSES)) {
            exportRepository.expenses(dateFilter).takeIf { it.isNotEmpty() }?.let {expenses ->
                hasItem = true
                currentEntity = "Expenses"
                currentCount = expenses.size
                val expensesSheet = workbook.createSheet("Expenses").apply {
                    createHeader(this, arrayOf("CREATED", "REMARKS", "AMOUNT",
                        "ADDED BY", "TAG"))
                }
                expenses.forEachIndexed { index, exportDataExpenses ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
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
        }

        if(inclusions.contains(INCLUDES_NEW_CUSTOMERS)) {
            exportRepository.customers(dateFilter).takeIf { it.isNotEmpty() }?.let {customers ->
                hasItem = true
                currentEntity = "Customers"
                currentCount = customers.size
                val customersWorksheet = workbook.createSheet("Customers").apply {
                    createHeader(this, arrayOf(
                        "CRN", "NAME", "CONTACT NUMBER", "ADDRESS", "EMAIL", "REMARKS", "TOTAL JOB ORDERS",
                        "NUMBER OF WASHES", "NUMBER OF DRIES", "FIRST JO", "LAST JO"
                    ))
                }
                customers.forEachIndexed { index, exportDataNewCustomers ->
                    if(stopFlag) return null
                    currentIndex = index + 1
                    prepareProgress()
                    customersWorksheet.createRow(index + 1).apply {
                        createCell(0).apply {
                            setCellValue(exportDataNewCustomers.crn)
                        }
                        createCell(1).apply {
                            setCellValue(exportDataNewCustomers.name)
                        }
                        createCell(2).apply {
                            setCellValue(exportDataNewCustomers.contactNumber)
                        }
                        createCell(3).apply {
                            setCellValue(exportDataNewCustomers.address)
                        }
                        createCell(4).apply {
                            setCellValue(exportDataNewCustomers.email)
                        }
                        createCell(5).apply {
                            setCellValue(exportDataNewCustomers.remarks)
                        }
                        createCell(6).apply {
                            setCellValue(exportDataNewCustomers.totalJobOrders.toString())
                        }
                        createCell(7).apply {
                            setCellValue(exportDataNewCustomers.totalWashes.toString())
                        }
                        createCell(8).apply {
                            setCellValue(exportDataNewCustomers.totalDries.toString())
                        }
                        createCell(9).apply {
                            setCellValue(exportDataNewCustomers.firstJo?.toShort())
                        }
                        createCell(10).apply {
                            setCellValue(exportDataNewCustomers.lastJo?.toShort())
                        }
                    }
                }
            }
        }

        return if(hasItem) workbook else null
    }

}