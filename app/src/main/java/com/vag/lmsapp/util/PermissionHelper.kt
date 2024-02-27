package com.vag.lmsapp.util

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class PermissionHelper(private val activity: AppCompatActivity) {
    protected abstract var permissions: Array<String>
    private var _onRequestGranted: (() -> Unit)? = null

    fun requestPermission() {
        if(hasPermissions()) {
            _onRequestGranted?.invoke()
        }
        permissionRequestLauncher.launch(permissions)
    }

    fun setOnRequestGranted(requestGranted: () -> Unit) {
        _onRequestGranted = requestGranted
    }

    private val permissionRequestLauncher = ActivityContractsLauncher(activity).apply {
        onOk = {
            requestGranted()
        }
    }

    protected open fun requestGranted() {
        _onRequestGranted?.invoke()
    }

    fun hasPermissions() : Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}