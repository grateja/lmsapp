package com.vag.lmsapp.app.joborders.create.packages.preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobOrderPackagePreviewViewModel

@Inject
constructor(
    private val repository: JobOrderPackageRepository
): ViewModel() {
    private val _packageId = MutableLiveData<UUID>()

    val jobOrderPackage = _packageId.switchMap { repository.getPackageWithItemsAsLiveData(it) }

    fun setId(packageId: UUID) {
        _packageId.value = packageId
    }
}