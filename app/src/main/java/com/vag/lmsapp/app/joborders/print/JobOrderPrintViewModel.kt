package com.vag.lmsapp.app.joborders.print

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity.Companion.TAB_CLAIM_STUB
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity.Companion.TAB_JOB_ORDER
import com.vag.lmsapp.app.joborders.print.JobOrderPrintActivity.Companion.TAB_MACHINE_STUB
import com.vag.lmsapp.model.EnumPaymentMethod
import com.vag.lmsapp.model.EnumPrintState
import com.vag.lmsapp.model.PrinterItem
//import com.csi.palabakosys.model.PrintItem
//import com.csi.palabakosys.room.repository.DataStoreRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.settings.PrinterSettingsRepository
import com.vag.lmsapp.settings.ShopPreferenceSettingsRepository
import com.vag.lmsapp.util.toShort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobOrderPrintViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository,
    shopPrefRepository: ShopPreferenceSettingsRepository,
    private val printerSettings: PrinterSettingsRepository
) : ViewModel() {
    private val showJoItemized = printerSettings.showJoItemized
    private val showJoPrices = printerSettings.showJoPrices
    private val showClaimStubItemized = printerSettings.showClaimStubItemized
    private val showClaimStubItemPrice = printerSettings.showClaimStubJoPrices
    private val showDisclaimer = printerSettings.showDisclaimer
    val characterLength = printerSettings.printerCharactersPerLine

    val isBluetoothAvailable = MutableLiveData<Boolean>()
    val bluetoothEnabled = MutableLiveData(false)
    val printerName = printerSettings.printerName

    private val _permitted = MutableLiveData<Boolean>()
    val permitted: LiveData<Boolean> = _permitted

    private val _printState = MutableLiveData(EnumPrintState.READY)
    val printState: LiveData<EnumPrintState> = _printState
    val buttonPrimaryAction = MediatorLiveData<String>().apply {
        addSource(_printState) {
            value = _printState.value?.let {
                when (it) {
                    EnumPrintState.STARTED -> "CANCEL"
                    EnumPrintState.ERROR -> "RETRY"
                    else -> "CONTINUE"
                }
            } ?: "CONTINUE"
        }
    }

    val canPrint = MediatorLiveData<Boolean>().apply {
        fun update() {
            val enabled = bluetoothEnabled.value ?: false
            val printState = _printState.value != EnumPrintState.STARTED
            val printerName = printerName.value != "Not set"
            value = enabled && printState && printerName
        }
        addSource(bluetoothEnabled) {update()}
        addSource(_printState) {update()}
        addSource(printerName) {update()}
    }
//    val printerName = dataStoreRepository.printerName
//    val printerAddress = dataStoreRepository.printerAddress

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    private val _selectedTab = MutableLiveData(TAB_CLAIM_STUB)
    val selectedTab: LiveData<String> = _selectedTab

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    private val _jobOrderId = MutableLiveData<UUID>()

    val jobOrderWithItems = _jobOrderId.switchMap { jobOrderRepository.getJobOrderWithItemsAsLiveData(it) }
    val unpaidJobOrders = jobOrderWithItems.switchMap { jobOrderRepository.getAllUnpaidByCustomerIdAsLiveData(it?.customer?.id) }

    val shopName = shopPrefRepository.shopName
    val address = shopPrefRepository.address
    val contactNumber = shopPrefRepository.contactNumber
    val email = shopPrefRepository.email
    private val disclaimer = printerSettings.jobOrderDisclaimer

//    val joDetails = MediatorLiveData<List<PrintItem>>().apply {
//        fun update() {
//            val jobOrder = jobOrderWithItems.value
//            value = listOf(
//                PrintItem("       DATE", jobOrder?.jobOrder?.createdAt?.toShort()),
//                PrintItem("        JO#", jobOrder?.jobOrder?.jobOrderNumber),
//                PrintItem("   CUSTOMER", jobOrder?.customer?.name),
//                PrintItem("PREPARED BY", jobOrder?.user?.name),
//            )
//        }
//        addSource(jobOrderWithItems) {update()}
//    }

    val contactInfo = MediatorLiveData<String?>().apply {
        fun update() {
            val email = email.value?.takeIf { it.isNotBlank() }
            val contactNumber = contactNumber.value?.takeIf { it.isNotBlank() }

            value = when {
                (email != null && contactNumber != null) -> "$contactNumber / $email"
                (email == null && contactNumber != null) -> contactNumber
                (email != null) -> email
                else -> ""
            }.takeIf { it.isNotBlank() }
        }
        addSource(contactNumber) {update()}
        addSource(email) {update()}
    }


    val items = MediatorLiveData<List<PrinterItem>>().apply {
        val handler = Handler(Looper.getMainLooper())
        fun update() {
            val tab = _selectedTab.value
            val itemizedJo = showJoItemized.value
            val itemizedClaimStub = showClaimStubItemized.value
            val characters = characterLength.value ?: 32
            val singleLine = PrinterItem.SingleLine(characters)
            val jobOrder = jobOrderWithItems.value

            val items = mutableListOf<PrinterItem>()

            if(tab == TAB_MACHINE_STUB) {
                jobOrder?.services?.forEach { service ->
                    for(i in 1..service.quantity) {
                        items.add(PrinterItem.TextCenter("DATE: ${jobOrder.jobOrder.createdAt.toShort()}"))
                        items.add(PrinterItem.HeaderDoubleCenter("JO#: ${jobOrder.jobOrder.jobOrderNumber}"))
                        items.add(PrinterItem.TextCenterTall(jobOrder.customer?.name))
                        if(i <= service.quantity) {
                            items.add(PrinterItem.Cutter(characters))
                        }
                    }
                }
            } else {
                val showPrice = showJoPrices.value == true && tab == TAB_JOB_ORDER || showClaimStubItemPrice.value == true && tab == TAB_CLAIM_STUB

                shopName.value?.takeIf { it.isNotBlank() }?.let {
                    items.add(PrinterItem.HeaderDoubleCenter(it))
                }

                address.value?.takeIf { it.isNotBlank() }?.let {
                    items.add(PrinterItem.TextCenter(it))
                }

                contactInfo.value?.takeIf { it.isNotBlank() }?.let {
                    items.add(PrinterItem.TextCenter(it))
                }

                items.apply {
                    add(singleLine)
                    add(PrinterItem.TextCenterTall("*** $tab ***"))
                    add(PrinterItem.SingleLine(characters))
                    add(PrinterItem.DefinitionTerm(
                        characters,
                        "DATE",
                        jobOrder?.jobOrder?.createdAt?.toShort())
                    )
                    add(PrinterItem.DefinitionTerm(characters, "JO#", jobOrder?.jobOrder?.jobOrderNumber))
                    add(PrinterItem.DefinitionTerm(characters, "CUSTOMER", jobOrder?.customer?.name))
                    add(PrinterItem.DefinitionTerm(characters, "PREPARED BY", jobOrder?.user?.name))
                    add(singleLine)
                }

                if((itemizedJo == true && tab == TAB_JOB_ORDER) || (itemizedClaimStub == true && tab == TAB_CLAIM_STUB)) {
                    items.addAll(
                        jobOrder?.services?.filter { !it.deleted }?.takeIf { it.isNotEmpty() }?.map {
                            PrinterItem.ListItem(characters, showPrice, it.quantity.toFloat(), it.serviceName, it.price * it.quantity)
                        }?.let { listOf(PrinterItem.Header("SERVICES")) + it } ?: emptyList()
                    )
                    items.addAll(
                        jobOrder?.products?.filter { !it.deleted }?.takeIf { it.isNotEmpty() } ?.map {
                            PrinterItem.ListItem(characters, showPrice, it.quantity.toFloat(), it.productName, it.price * it.quantity)
                        }?.let { listOf(PrinterItem.Header("PRODUCTS")) + it } ?: emptyList()
                    )
                    items.addAll(
                        jobOrder?.extras?.filter { !it.deleted }?.takeIf { it.isNotEmpty() }?.map {
                            PrinterItem.ListItem(characters, showPrice, it.quantity.toFloat(), it.extrasName, it.price * it.quantity)
                        }?.let { listOf(PrinterItem.Header("EXTRAS")) + it } ?: emptyList()
                    )
                    items.addAll(
                        jobOrder?.deliveryCharge?.takeIf { !it.deleted }?.let {
                            listOf(
                                PrinterItem.Header(it.deliveryOption.value),
                                PrinterItem.ListItem(characters, showPrice, it.distance, "KM${it.vehicle.value}", it.price)
                            )
                        } ?: emptyList()
                    )
                    items.add(singleLine)
                }

                val subtotal = jobOrder?.subtotal() ?: 0.0f
                val discountInPeso = jobOrder?.takeIf { it.discount?.deleted == false }?.discountInPeso() ?: 0.0f

                val discount = jobOrder?.takeIf { it.discount?.deleted == false }?.discount?.let { discount ->
                    PrinterItem.ListItem(characters, true, 0f, "${discount.name} discount", discountInPeso)
                }

                if(discount != null) {
                    items.add(PrinterItem.ListItem(characters, true,0f, "SUBTOTAL" , subtotal))
                    items.add(discount)
                }

                items.add(PrinterItem.ListItemBold(characters, true, 0f, "TOTAL", subtotal - discountInPeso))

                jobOrder?.paymentWithUser?.payment.let {
                    items.add(singleLine)
                    if(it != null && jobOrder != null) {
                        items.add(PrinterItem.DefinitionTerm(characters,"DATE PAID", it.createdAt.toShort()))
                        items.add(PrinterItem.DefinitionTerm(characters,"PMT. METHOD", it.method()))
                        if (it.paymentMethod == EnumPaymentMethod.CASHLESS) {
                            items.add(PrinterItem.DefinitionTerm(characters,"REF#", it.entityCashless?.refNumber))
                        }

                        it.orNumber?.takeIf { it.isNotBlank() }?.let {
                            items.add(PrinterItem.DefinitionTerm(characters,"OR NUMBER", it))
                        }

                        items.add(PrinterItem.DefinitionTerm(characters,"RECEIVED BY", jobOrder.paymentWithUser?.user?.name))
                    } else {
                        items.add(PrinterItem.TextCenterTall("~ UNPAID ~"))
                    }
                }

                if(showDisclaimer.value == true) {
                    items.add(singleLine)
                    items.add(
                        PrinterItem.TextCenter(disclaimer.value)
                    )
                }
            }

            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                value = items // details + services + products + extras + delivery
            }, 500)
        }

        addSource(shopName) {update()}
        addSource(address) {update()}
        addSource(contactInfo) {update()}
        addSource(jobOrderWithItems) {update()}
        addSource(_selectedTab) {update()}
        addSource(showJoItemized) {update()}
        addSource(showJoPrices) {update()}
        addSource(showClaimStubItemized) {update()}
        addSource(showClaimStubItemPrice) {update()}
        addSource(showDisclaimer) {update()}
        addSource(disclaimer) {update()}
    }

//    val summary = MediatorLiveData<List<PrintItem>>().apply {
//        addSource(jobOrderWithItems) {
//            val jobOrder = jobOrderWithItems.value
//
//            val subtotal = jobOrder?.subtotal() ?: 0.0f
//            val discountInPeso = jobOrder?.discountInPeso() ?: 0.0f
//
//            val discount = jobOrder?.discount?.let { discount ->
//                PrintItem(null, "${discount.name} discount", discountInPeso)
//            }
//
//            val items = mutableListOf<PrintItem>()
//            if(discount != null) {
//                items.add(PrintItem(null, "SUBTOTAL" , subtotal))
//                items.add(discount)
//            }
//
//            items.add(PrintItem(null, "TOTAL", subtotal - discountInPeso))
//
//            value = items
//        }
//    }

//    val totalAmountDue = MediatorLiveData<PrintItem>().apply {
//        addSource(jobOrderWithItems) {
//            val subtotal = jobOrderWithItems.value?.subtotal() ?: 0.0f
//            val discountInPeso = jobOrderWithItems.value?.discountInPeso() ?: 0.0f
//
//            val title = if (discountInPeso > 0) "DISCOUNTED AMOUNT" else "TOTAL"
//            val total = subtotal - discountInPeso
//
//            value = PrintItem(null, title, total)
//        }
//    }

//    val services = MediatorLiveData<List<PrintItem>>().apply {
//        fun update() {
//            val items = jobOrderWithItems.value?.services?.map {
//                PrintItem(
//                    it.quantity, it.serviceName, it.price * it.quantity
//                )
//            }
//            if(!items.isNullOrEmpty()) {
//                value = listOf(PrintItem("SERVICES")) + items
//            }
//        }
//        addSource(jobOrderWithItems){update()}
//    }

//    val products = MediatorLiveData<List<PrintItem>>().apply {
//        fun update() {
//            val items = jobOrderWithItems.value?.products?.map {
//                PrintItem(
//                    it.quantity, it.productName, it.price * it.quantity
//                )
//            }
//            if(!items.isNullOrEmpty()) {
//                value = listOf(PrintItem("PRODUCTS")) + items
//            }
//        }
//        addSource(jobOrderWithItems){update()}
//    }
//
//    val extras = MediatorLiveData<List<PrintItem>?>().apply {
//        fun update() {
//            val items = jobOrderWithItems.value?.extras?.map {
//                PrintItem(
//                    it.quantity, it.extrasName, it.price * it.quantity
//                )
//            }
//            if(!items.isNullOrEmpty()) {
//                value = listOf(PrintItem("EXTRAS")) + items
//            }
//        }
//        addSource(jobOrderWithItems){update()}
//    }

//    val unpaid = MediatorLiveData<List<PrintItem>?>().apply {
//        fun update() {
//            val jobOrderId = _jobOrderId.value
//
//            value = listOf(PrintItem("UNPAID JOB ORDERS")) + (unpaidJobOrders.value?.filter {
//                it.id != jobOrderId
//            }?.map {
//                PrintItem(null, it.jobOrderNumber, it.discountedAmount)
//            } ?: emptyList())
//        }
//        addSource(unpaidJobOrders) {update()}
//    }
//
//    val hasServices = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.services?.isNotEmpty()
//        }
//        addSource(jobOrderWithItems) {update()}
//    }
//
//    val hasProducts = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.products?.isNotEmpty()
//        }
//        addSource(jobOrderWithItems) {update()}
//    }
//
//    val hasExtras = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.extras?.isNotEmpty()
//        }
//        addSource(jobOrderWithItems) {update()}
//    }
//
//    val hasPickupAndDelivery = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.deliveryCharge != null
//        }
//        addSource(jobOrderWithItems) {update()}
//    }

//    val hasPayment = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.paymentWithUser != null
//        }
//        addSource(jobOrderWithItems) {update()}
//    }

//    val hasUnpaidJobOrders = MediatorLiveData<Boolean>().apply {
//        fun update() {
//            value = unpaid.value?.isNotEmpty()
//        }
//        addSource(unpaid) {update()}
//    }


//    val deliveryOption = MediatorLiveData<String>().apply {
//        fun update() {
//            value = jobOrderWithItems.value?.deliveryCharge?.deliveryOption?.value
//        }
//
//        addSource(jobOrderWithItems){update()}
//    }

//    val currentAmountDue = MediatorLiveData<String>().apply {
//        fun update() {
//            val jobOrder = jobOrderWithItems.value
//            value = jobOrder?.discount?.let {
//                // subtotal
//                // discounted amount
//                "[L]SUBTOTAL[R]${jobOrder.subtotal()}\n" +
//                "[L]DISCOUNTED AMOUNT[R]"
//            } ?: "keme"
//        }
//        addSource(jobOrderWithItems) {jobOrderWithItems}
//    }

//    val paymentDetails = MediatorLiveData<List<PrintItem>>().apply {
//        fun update() {
//            val items = mutableListOf<PrintItem>()
//            val jobOrder = jobOrderWithItems.value
//            val payment = jobOrder?.paymentWithUser?.payment
//
//            payment.let {
//                if(it != null && jobOrder != null) {
//                    items.add(PrintItem("  DATE PAID", it.createdAt.toShort()))
//                    items.add(PrintItem("PMT. METHOD", it.method()))
//                    if (it.paymentMethod == EnumPaymentMethod.CASHLESS) {
//                        items.add(PrintItem("       REF#", it.entityCashless?.refNumber))
//                    }
//
//                    it.orNumber?.let { orNumber ->
//                        items.add(PrintItem("  OR NUMBER", orNumber))
//                    }
//
//                    items.add(PrintItem("RECEIVED BY", jobOrder.paymentWithUser?.user?.name))
//                } else {
//                    items.add(PrintItem("UNPAID"))
//                }
//            }
//
//            value = items
//        }
//
//        addSource(jobOrderWithItems) { update() }
//    }


//    val paymentDetails = MediatorLiveData<List<PrintItem>>().apply {
//        fun update() {
//            val items = mutableListOf<PrintItem>()
//            val jobOrder = jobOrderWithItems.value
//            val payment = jobOrder?.paymentWithUser?.payment
//
//            if(payment != null) {
//                items.add(PrintItem("  DATE PAID:", payment.createdAt.toShort()))
//                items.add(PrintItem("PMT. METHOD:", payment.method()))
//                items.add(PrintItem("   AMT. DUE:", "P${payment.amountDue}"))
//                items.add(PrintItem(" AMT. RECVD:", "P${payment.cashReceived}"))
//                if(payment.change() > 0) {
//                    items.add(PrintItem("     CHANGE:", "P${payment.change()}"))
//                }
//                if(payment.paymentMethod == EnumPaymentMethod.CASHLESS) {
//                    items.add(PrintItem("       REF#:", payment.entityCashless?.refNumber))
//                }
//                if(payment.orNumber != null) {
//                    items.add(PrintItem("  OR NUMBER:", payment.orNumber))
//                }
//                items.add(PrintItem("RECEIVED BY:", jobOrder.paymentWithUser?.user?.name))
//            }
//            value = items
//        }
//        addSource(jobOrderWithItems) {update()}
//    }

    fun setJobOrderId(id: UUID) {
        _jobOrderId.value = id
    }

    fun print() {
        if(_printState.value == EnumPrintState.STARTED) {
            _dataState.value = DataState.Cancel
            return
        }

        val characterLength = characterLength.value ?: 32

        val singleLine = PrinterItem.SingleLine(characterLength)

        val header = "[C]<font size='tall'><b>*** ${_selectedTab.value} ***</b></font>\n"

        val shopName = shopName.value.let { "[C]<font size='big'><b>$it</b></font>\n" }
        val address = address.value?.let { "[C]<font size='small'>$it</font>\n" } ?: ""
        val contactNumber = contactNumber.value
        val email = email.value

        val contactInfo = when {
            !contactNumber.isNullOrBlank() && !email.isNullOrBlank() -> "[C]<font size='small'>$contactNumber/$email</font>\n"
            !contactNumber.isNullOrBlank() -> "[C]<font size='small'>$contactNumber</font>\n"
            !email.isNullOrBlank() -> "[C]<font size='small'>$email</font>\n"
            else -> ""
        }

//        val joDetails = joDetails.value?.takeIf { it.isNotEmpty() }?.let {
//            it.joinToString("") { it.formattedString() }
//        }

        val items = items.value.let {
            it?.joinToString ("\n") { it.formattedString() }
        }

//        val summary = summary.value.let {
//            it?.joinToString("") { it.formattedString() }
//        }
//
//        val paymentDetails = paymentDetails.value?.takeIf { it.isNotEmpty() }?.let {
//            it.joinToString("") { it.formattedString() }
//        } ?: ""

        val disclaimer =  PrinterItem.Header(disclaimer.value).formattedString()

        val formattedText = items?:"" //"$shopName$address$contactInfo" +
//                header +
//                joDetails +
//                singleLine +
//                items// +
//                singleLine +
//                summary +
//                singleLine +
//                paymentDetails +
//                disclaimer

        println(formattedText)

         _dataState.value = DataState.Submit(formattedText)
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun setBluetoothState(state: Boolean) {
        bluetoothEnabled.value = state
    }

    fun setDevice(device: PrinterDevice?) {
        viewModelScope.launch {
            printerSettings.update(device?.deviceName, PrinterSettingsRepository.PRINTER_NAME)
            printerSettings.update(device?.macAddress, PrinterSettingsRepository.PRINTER_ADDRESS)
        }
    }

    fun setPrintState(printState: EnumPrintState) {
        _printState.value = printState
    }

    fun setBluetoothAvailability(available: Boolean) {
        isBluetoothAvailable.value = available
    }

    fun setPermissionStatus(status: Boolean) {
        _permitted.value = status
    }

    sealed class DataState {
        object StateLess: DataState()
        object Cancel: DataState()
        class Submit(val formattedText: String): DataState()
    }
}