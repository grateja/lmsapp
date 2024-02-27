package com.vag.lmsapp.settings

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderSettingsRepository

@Inject
constructor(
    private val dao: SettingsDao
) : BaseSettingsRepository(dao) {
    companion object {
        const val JOB_ORDER_MAX_UNPAID = "jobOrderSettingsMaxUnpaidLimitKeme"
        const val JOB_ORDER_REQUIRE_OR_NUMBER = "requireOrNumberKeme"
    }
    val maxUnpaidJobOrderLimit = getAsLiveData(JOB_ORDER_MAX_UNPAID, 3)
    val requireOrNumber = getAsLiveData(JOB_ORDER_REQUIRE_OR_NUMBER, false)

    suspend fun updateRequireOrNumber(value: Boolean) {
        update(value, JOB_ORDER_REQUIRE_OR_NUMBER)
    }
}