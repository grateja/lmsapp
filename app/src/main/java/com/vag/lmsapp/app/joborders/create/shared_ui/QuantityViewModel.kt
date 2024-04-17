package com.vag.lmsapp.app.joborders.create.shared_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuantityViewModel

@Inject
constructor() : ViewModel()
{
    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    val model = MutableLiveData<QuantityModel>()
    fun setData(data: QuantityModel) {
        model.value = data
    }

    fun confirm() {
        model.value?.let {
            _dataState.value = DataState.Confirm(it)
        }
    }

    fun remove() {
        model.value?.let {
            _dataState.value = DataState.Remove(it)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    sealed class DataState {
        data object StateLess: DataState()
        data class Confirm(val model: QuantityModel): DataState()
        data class Remove(val model: QuantityModel): DataState()
    }
}