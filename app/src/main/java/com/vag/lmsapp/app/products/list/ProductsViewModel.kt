package com.vag.lmsapp.app.products.list

import androidx.lifecycle.*
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.app.products.list.advanced_filter.ProductListAdvancedFilter
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel

@Inject
constructor(
    private val repository: ProductRepository
) : ListViewModel<ProductItemFull, ProductListAdvancedFilter>() {

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

            keyword.value?.let {
                repository.filter(it, page, filterParams.value).let {
                    setResult(it.items, it.resultCount, reset)
                }
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: ProductListAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }
}