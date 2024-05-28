//package com.vag.lmsapp.settings
//
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class ShopPreferenceSettingsRepository
//
//@Inject
//constructor(
//    private val dao: SettingsDao
//) : BaseSettingsRepository(dao) {
//    companion object {
//        const val SHOP_NAME = "shopName"
//        const val ADDRESS = "address"
//        const val CONTACT_NUMBER = "contactNumber"
//        const val EMAIL = "email"
//    }
//    val shopName = getAsLiveData(SHOP_NAME, "")
//    val address = getAsLiveData(ADDRESS, "")
//    val contactNumber = getAsLiveData(CONTACT_NUMBER, "")
//    val email = getAsLiveData(EMAIL, "")
//}