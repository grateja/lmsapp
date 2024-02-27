package com.vag.lmsapp.app.joborders.create.shared_ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuantityViewModel

@Inject
constructor() : ViewModel()
{
    val model = MutableLiveData<QuantityModel>()
    fun setData(data: QuantityModel) {
        model.value = data
    }
}