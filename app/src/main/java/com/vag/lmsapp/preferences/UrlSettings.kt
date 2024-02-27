package com.vag.lmsapp.preferences

class UrlSettings(
    var endPoint: String,
    var connectionTimeout: Long
) {
    fun toString(machineIp: String) : String {
        return "http://$machineIp/$endPoint"
    }
}