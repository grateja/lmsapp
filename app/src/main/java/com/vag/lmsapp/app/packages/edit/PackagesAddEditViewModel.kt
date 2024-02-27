package com.vag.lmsapp.app.packages.edit

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityPackage
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PackagesAddEditViewModel

@Inject
constructor(
    private val repository: JobOrderPackageRepository
) : CreateViewModel<EntityPackage>(repository) {
    fun get(id: UUID?) {
        viewModelScope.launch {
            val entity = super.get(id, EntityPackage())
        }
    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("packageName", model.value?.packageName, arrayOf(Rule.Required))
        }

        super.isInvalid(inputValidation)
    }
}