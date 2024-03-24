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

        const val SHOP_NAME = "shopName"
        const val ADDRESS = "address"
        const val CONTACT_NUMBER = "contactNumber"
        const val EMAIL = "email"
    }
    val maxUnpaidJobOrderLimit = getAsLiveData(JOB_ORDER_MAX_UNPAID, 3)
    val requireOrNumber = getAsLiveData(JOB_ORDER_REQUIRE_OR_NUMBER, false)
    val shopName = getAsLiveData(SHOP_NAME, "")
    val address = getAsLiveData(ADDRESS, "")
    val contactNumber = getAsLiveData(CONTACT_NUMBER, "")
    val email = getAsLiveData(EMAIL, "")

    suspend fun updateRequireOrNumber(value: Boolean) {
        update(value, JOB_ORDER_REQUIRE_OR_NUMBER)
    }
}