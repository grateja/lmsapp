package com.vag.lmsapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.ListViewModelInterface
import com.vag.lmsapp.util.ResultCount
import kotlinx.coroutines.Job

abstract class ListViewModel<T, F: BaseFilterParams> : ViewModel(), ListViewModelInterface {
    private val _filterState = MutableLiveData<FilterState<T, F>>()
    val filterState: LiveData<FilterState<T, F>> = _filterState

    private val _items = MutableLiveData<MutableList<T>>()
    val items: LiveData<MutableList<T>> = _items

    private val _resultCount = MutableLiveData<ResultCount?>()
    val resultCount: LiveData<ResultCount?> = _resultCount

    override val keyword = MutableLiveData("")
    val filterParams = MutableLiveData<F>()

    val loading = MutableLiveData(false)

    val hasItems = MediatorLiveData<Boolean>().apply {
        addSource(_items) {
            value = it.isNotEmpty()
        }
    }

    protected var page: Int = 1
    protected var job: Job? = null

    protected fun setResult(items: List<T>, resultCount: ResultCount?, reset: Boolean) {
        if(reset) {
            _items.value = items.toMutableList()
        } else {
            _items.value = _items.value?.apply { this + items }
        }

        _filterState.value = FilterState.LoadItems(items, reset)
        _resultCount.value = resultCount
    }

    protected fun setFilterState(filterState: FilterState<T, F>) {
        _filterState.value = filterState
    }

    fun setDateFilter(dateFilter: DateFilter?) {
        filterParams.value = filterParams.value.apply {
            this?.dateFilter = dateFilter
        }
    }

    open fun setFilterParams(filterParams: F) {
        this.filterParams.value = filterParams
    }

    open fun clearState() {
        _filterState.value = FilterState.StateLess()
    }

    fun loadMore() {
        ++page
        filter(false)
    }
}