package com.vag.lmsapp.app.extras.list

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.extras.list.advanced_filter.ExtrasAdvancedFilter
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtrasViewModel

@Inject
constructor(
    private val repository: ExtrasRepository
) : ListViewModel<ExtrasItemFull, ExtrasAdvancedFilter>() {

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true

            delay(500)

            if(reset) {
                page = 1
            }

            val filter = filterParams.value ?: ExtrasAdvancedFilter()
            repository.filter(keyword.value ?: "", page, filter).let {
                setResult(it.items, it.resultCount, reset)
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: ExtrasAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }
}