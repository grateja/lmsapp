package com.vag.lmsapp.app.products

import androidx.lifecycle.*
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ProductRepository
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
) : ListViewModel<ProductItemFull, BaseFilterParams>() {

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true
            delay(500)
            keyword.value?.let {
                items.value = repository.filter(it)
            }
        }
    }
}