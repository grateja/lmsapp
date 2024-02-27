package com.vag.lmsapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.util.ListViewModelInterface
import kotlinx.coroutines.Job

abstract class ListViewModel<T, F: BaseFilterParams?> : ViewModel(), ListViewModelInterface {
    protected val _dataState = MutableLiveData<DataState<T>>()
    val dataState: LiveData<DataState<T>> = _dataState

    override val keyword = MutableLiveData("")
    val filterParams = MutableLiveData<F>()

    val items = MutableLiveData<List<T>>()
//    val orderBy = MutableLiveData("created_at")
//    val sortDirection = MutableLiveData<EnumSortDirection>()
    val page = MutableLiveData(1)
    val loading = MutableLiveData(false)

    protected var job: Job? = null

    open fun setFilterParams(filterParams: F) {
        this.filterParams.value = filterParams
    }

    open fun clearState() {
        _dataState.value = DataState.StateLess
    }

    sealed class DataState<out R> {
        data class LoadItems<R>(val items: List<R>, val reset: Boolean) : DataState<R>()
        object StateLess: DataState<Nothing>()
    }
}