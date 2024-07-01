package com.vag.lmsapp.app.packages.preview.modify_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.app.packages.PackageItem
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation

class PackagePreviewSelectedItemViewModel : ViewModel() {
    private val _dataState = MutableLiveData<DataState<PackageItem>>()
    val dataState: LiveData<DataState<PackageItem>> = _dataState

    val unitPrice = MutableLiveData("")
    val quantity = MutableLiveData("")

    private lateinit var _packageItem: PackageItem

    private val _validation = MutableLiveData(InputValidation())
    val validation: LiveData<InputValidation> = _validation

    fun setPackageItem(packageItem: PackageItem) {
        _packageItem = packageItem
        unitPrice.value = packageItem.price.toString()
        quantity.value = packageItem.quantity.toString()
    }

    fun clearError(key: String) {
        _validation.value = _validation.value?.removeError(key)
    }

    fun remove() {
        _packageItem.deleted = true
        _dataState.value = DataState.DeleteSuccess(_packageItem)
    }

    fun validate() {
        _validation.value = InputValidation().apply {
            addRule("unitPrice", unitPrice.value, arrayOf(Rule.Required, Rule.IsNumeric))
            addRule("quantity", quantity.value, arrayOf(Rule.Required, Rule.IsNumeric))

            if(isInvalid()) {
                _dataState.value = DataState.InvalidInput(this)
            } else {
                val quantity = quantity.value?.toInt() ?: return
                val unitPrice = unitPrice.value?.toFloat() ?: return

                _packageItem = _packageItem.apply {
                    this.quantity = quantity
                    this.price = unitPrice
                    this.deleted = false
                }
                _dataState.value = DataState.SaveSuccess(_packageItem)
            }
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}