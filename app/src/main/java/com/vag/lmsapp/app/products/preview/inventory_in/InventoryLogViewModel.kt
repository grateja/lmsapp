package com.vag.lmsapp.app.products.preview.inventory_in

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.room.repository.InventoryLogRepository
import com.vag.lmsapp.util.isNotEmpty
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InventoryLogViewModel

@Inject
constructor(
    private val repository: InventoryLogRepository
): ListViewModel<EntityInventoryLogFull, InventoryLogAdvancedFilter>()
{
//    private val _productId = MutableLiveData<UUID?>()
//    val productId: LiveData<UUID?> = _productId
//
    fun setProductId(productId: UUID?) {
        val filter = filterParams.value.apply {
            this?.productId = productId
        } ?: InventoryLogAdvancedFilter(productId)
        setFilterParams(filter)
    }

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            if(keyword.value.isNotEmpty()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page = 1
            }

            repository.queryResult(keyword.value, filterParams.value, page).let {
                setResult(it.result, it.resultCount, reset)
            }
        }
    }
}