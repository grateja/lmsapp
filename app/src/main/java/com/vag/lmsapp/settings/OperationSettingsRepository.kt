package com.vag.lmsapp.settings

import androidx.lifecycle.MediatorLiveData
import com.squareup.moshi.Moshi
import com.vag.lmsapp.util.DateFilter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OperationSettingsRepository

@Inject
constructor(
    private val dao: SettingsDao,
    private val moshi: Moshi
): BaseSettingsRepository(dao) {
    companion object {
        const val LAST_EXPORT_DATE_FILTER = "last_export_date_filters"
    }

//    private val _dateAdapter = moshi.adapter(DateFilter::class.java)
    private val _dateFilterStr = moshi.adapter(DateFilter::class.java).toJson(DateFilter())
    private val _lastExportDateFilter = getAsLiveData(LAST_EXPORT_DATE_FILTER, _dateFilterStr)

    val dateFilter = MediatorLiveData<DateFilter>().apply {
        addSource(_lastExportDateFilter) {
            value = moshi.adapter(DateFilter::class.java).fromJson(it)
        }
    }

    suspend fun setDateFilter(dateFilter: DateFilter) {
        val string = moshi.adapter(DateFilter::class.java).toJson(dateFilter)
        update(string, LAST_EXPORT_DATE_FILTER)
    }
}