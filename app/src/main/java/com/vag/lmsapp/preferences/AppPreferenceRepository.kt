package com.vag.lmsapp.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceRepository

@Inject
constructor (@ApplicationContext context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var ipSettings = IpSettings(
        sharedPreferences.getString("networkPrefix", null) ?: "192.168",
        sharedPreferences.getInt("subnetId", 210)
    )

    var urlSettings = UrlSettings(
        sharedPreferences.getString("endPoint", "initiate") ?: "",
        sharedPreferences.getLong("connectionTimeout", 5)
    )

    fun setCurrentPrinter(printerSettings: PrinterSettings) {
        with(sharedPreferences.edit()) {
            putString("printer_name", printerSettings.name)
            putString("printer_address", printerSettings.address)
//            putInt("printer_dpi", printerSettings.dpi)
            putFloat("printer_width", printerSettings.width)
            putInt("printer_character", printerSettings.character)
            apply()
        }
    }

//    fun printerSettings(): PrinterSettings {
//        return PrinterSettings(
//            sharedPreferences.getString("printer_name", "Tap to select printer") ?: "keme",
//            sharedPreferences.getString("printer_address", "Tap to select printer"),
////            sharedPreferences.getInt("printer_dpi", 203),
//            sharedPreferences.getFloat("printer_width", 58f),
//            sharedPreferences.getInt("printer_character", 32)
//        )
//    }

    fun saveIpSettings(ip: IpSettings) {
        with(sharedPreferences.edit()) {
            putString("networkPrefix", ip.networkPrefix)
            putInt("subnetId", ip.subnetId)
            apply()
        }
    }

    fun saveUrlSettings(urlSettings: UrlSettings) {
        with(sharedPreferences.edit()) {
            putString("endPoint", urlSettings.endPoint)
            apply()
        }
    }

    fun requireORNumber(): Boolean {
        return sharedPreferences.getBoolean("requireORNumber", true)
    }

//    fun testFakeConnect(): Boolean {
//        return sharedPreferences.getBoolean("testConnectMode", true)
//    }
//
//    fun setFakeConnectionMode(value: Boolean) {
//        with(sharedPreferences.edit()) {
//            putBoolean("testConnectMode", value)
//            apply()
//        }
//    }
//
//    fun testFakeDelay(): Long {
//        return sharedPreferences.getLong("testFakeDelay", 1000)
//    }
//
//    fun setFakeConnectionDelay(value: Long) {
//        with(sharedPreferences.edit()) {
//            putLong("testFakeDelay", value)
//            apply()
//        }
//    }
}