package com.vag.lmsapp.app.lms_live.sync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel

@Inject
constructor(): ViewModel(){
    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title

    fun setTitle(title: String?) {
        _title.value = title
    }
}