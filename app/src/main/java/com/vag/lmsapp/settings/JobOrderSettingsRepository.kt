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
        const val JOB_ORDER_REQUIRE_PICTURE_ON_CASHLESS_PAYMENT = "requirePictureOnCashlessPayment"

        const val SHOP_NAME = "name"
        const val ADDRESS = "address"
        const val CONTACT_NUMBER = "contact_number"
        const val EMAIL = "email"
    }
    val maxUnpaidJobOrderLimit = getAsLiveData(JOB_ORDER_MAX_UNPAID, 3)
    val requireOrNumber = getAsLiveData(JOB_ORDER_REQUIRE_OR_NUMBER, false)
    val shopName = getAsLiveData(SHOP_NAME, "")
    val address = getAsLiveData(ADDRESS, "")
    val contactNumber = getAsLiveData(CONTACT_NUMBER, "")
    val email = getAsLiveData(EMAIL, "")
    val requirePictureOnCashlessPayment = getAsLiveData(
        JOB_ORDER_REQUIRE_PICTURE_ON_CASHLESS_PAYMENT, false)

    suspend fun updateRequireOrNumber(value: Boolean) {
        update(value, JOB_ORDER_REQUIRE_OR_NUMBER)
    }

    suspend fun updateRequirePictureOnCashlessPayment(value: Boolean) {
        update(value, JOB_ORDER_REQUIRE_PICTURE_ON_CASHLESS_PAYMENT)
    }
}