package com.vag.lmsapp.app.discounts.list

import androidx.lifecycle.*
import com.vag.lmsapp.room.entities.EntityDiscount
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscountsViewModel

@Inject
constructor(
    private val repository: DiscountsRepository
) : ListViewModel<EntityDiscount, Nothing>() {

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true
            delay(500)
            keyword.value?.let {
                val items = repository.filter(it)
                setResult(items, null, reset)
            }
        }
    }
}