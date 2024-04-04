package com.vag.lmsapp.app.joborders.payment.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobOrderPaymentDeleteViewModel

@Inject
constructor(
    private val paymentRepository: PaymentRepository
): ViewModel(){
    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    private val _validation = MutableLiveData(InputValidation())
    val validation: LiveData<InputValidation> = _validation

    val remarks = MutableLiveData("")

    private val _paymentId = MutableLiveData<UUID>()
    val payment = _paymentId.switchMap { paymentRepository.getPaymentWithJobOrdersAsLiveData(it) }

    fun setPaymentId(paymentId: UUID) {
        _paymentId.value = paymentId
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun clearError(key: String) {
        _validation.value = _validation.value?.removeError(key)
    }

    fun confirm(userId: UUID) {
        viewModelScope.launch {
            val inputValidation = InputValidation().apply {
                addRule("remarks", remarks.value, arrayOf(Rule.Required))
            }
            if(inputValidation.isInvalid()) {
                _validation.value = inputValidation
            } else {
                val paymentId = _paymentId.value ?: return@launch
                val remarks = remarks.value ?: return@launch
                paymentRepository.delete(
                    paymentId, userId, remarks
                )
                _dataState.value = DataState.SaveSuccess(paymentId)
            }
        }
    }
}