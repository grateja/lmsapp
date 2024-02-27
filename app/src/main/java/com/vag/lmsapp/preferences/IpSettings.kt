package com.vag.lmsapp.preferences

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