package com.vag.lmsapp.app.app_settings.developer

class UrlSettings(
    var endPoint: String,
    var connectionTimeout: Long
) {
    fun toString(machineIp: String) : String {
        return "http://$machineIp/$endPoint"
    }
}