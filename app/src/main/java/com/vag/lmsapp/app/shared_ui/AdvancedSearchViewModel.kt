package com.vag.lmsapp.app.shared_ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.csi.palabakosys.util.EnumSortDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvancedSearchViewModel

@Inject
constructor()
: ViewModel() {
    val keyword = MutableLiveData("")
    val itemPerPage = MutableLiveData("")
    val sortDirection = MutableLiveData("DESC")
}