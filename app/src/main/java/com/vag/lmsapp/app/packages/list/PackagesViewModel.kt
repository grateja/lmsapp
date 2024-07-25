package com.vag.lmsapp.app.packages.list

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.entities.EntityPackageWithItems
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel

@Inject
constructor(
    private val repository: JobOrderPackageRepository
) : ListViewModel<EntityPackageWithItems, BaseFilterParams>() {
//    val packages = repository.getAllAsLiveData()

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true
            delay(500)
            keyword.value?.let {
                repository.filter(it).let {
                    setResult(it, null, reset)
                }
            }
        }
    }
}