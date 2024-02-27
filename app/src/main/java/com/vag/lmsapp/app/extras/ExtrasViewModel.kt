package com.vag.lmsapp.app.extras

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ExtrasRepository
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
) : ListViewModel<ExtrasItemFull, BaseFilterParams>() {

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