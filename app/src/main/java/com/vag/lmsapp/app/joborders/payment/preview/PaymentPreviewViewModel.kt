//package com.vag.lmsapp.app.joborders.payment.preview
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.switchMap
//import com.vag.lmsapp.room.repository.CustomerRepository
//import com.vag.lmsapp.room.repository.PaymentRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import java.util.*
//import javax.inject.Inject
//
//@HiltViewModel
//class PaymentPreviewViewModel
//
//@Inject
//constructor(
//    private val paymentRepository: PaymentRepository,
//    private val customerRepository: CustomerRepository
//) : ViewModel() {
//    fun setPaymentId(paymentId: UUID) {
//        _paymentId.value = paymentId
//    }
//
//    private val _paymentId = MutableLiveData<UUID>()
//    val payment = _paymentId.switchMap { paymentRepository.getPaymentWithJobOrders(it) }
//
//    val jobOrders = _paymentId.switchMap { paymentRepository.getJobOrdersByPaymentId(it) }
//
//    val customer = jobOrders.switchMap { customerRepository.getCustomerAsLiveData(it.first().customerId) }
//}