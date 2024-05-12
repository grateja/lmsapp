package com.vag.lmsapp.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.widget.Toast

class NetworkHelper(val context: Context) {

    fun isWifiConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true && activeNetwork.type == ConnectivityManager.TYPE_WIFI
    }

    fun isMobileDataOn(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_MOBILE
    }

    fun suggestWifiNetwork(ssid: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()
            val suggestionsList = mutableListOf(suggestion)
            wifiManager.addNetworkSuggestions(suggestionsList)
        } else {
            Toast.makeText(context, "Please connect to the Wi-Fi network manually in settings.", Toast.LENGTH_LONG).show()
        }
    }

    fun removeWifiSuggestion(ssid: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()
            val suggestionsList = mutableListOf(suggestion)
            wifiManager.removeNetworkSuggestions(suggestionsList)
        } else {
            Toast.makeText(context, "Please connect to the Wi-Fi network manually in settings.", Toast.LENGTH_LONG).show()
        }
    }

//    fun scanForWifiNetworks(wifiScanCallback: (List<ScanResult>) -> Unit) {
//        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val success = intent?.getBooleanExtra(WifiManager.EXTRA_SCAN_SUCCESS, false) ?: false
//                if (success) {
//                    val results = wifiManager.scanResults
//                    wifiScanCallback(results)
//                }
//                context?.unregisterReceiver(this)
//            }
//        }
//
//        context.registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_BROADCAST_ACTION))
//        wifiManager.startScan()
//    }

    fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}