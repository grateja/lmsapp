package com.vag.lmsapp.app.app_settings.developer

import java.net.InetAddress

class IpSettings (
    var networkPrefix: String,
    var subnetId: Int,
) {
    fun toString(hostId: Int) : String {
        return "$networkPrefix.$subnetId.$hostId"
    }

    fun test() {
        InetAddress.getLocalHost()
    }
}