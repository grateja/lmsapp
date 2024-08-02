package com.vag.lmsapp.util.file

import android.os.Build
import android.os.FileObserver
import java.io.File

open class FileObserverApiWrapper(path: String, mask: Int) {
    private var fileObserver: FileObserver? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileObserver = object : FileObserver(File(path), mask) {
                override fun onEvent(event: Int, path: String?) {
                    _onEvent(event, path)
//                    this@FileObserverApiWrapper.onEvent(event,path)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            fileObserver = object : FileObserver(path, mask) {
                override fun onEvent(event: Int, path: String?) {
                    _onEvent(event, path)
//                    this@FileObserverApiWrapper.onEvent(event,path)
                }
            }
        }
    }

    /**
     * @description does nothing, can be overridden. Equivalent to FileObserver.onEvent
     */
    var onFileCreated: (() -> Unit)? = null
    var onFileDeleted: (() -> Unit)? = null

    private fun _onEvent(event: Int, path: String?) {
        when (event) {
            FileObserver.CREATE -> onFileCreated?.invoke()
            FileObserver.DELETE -> onFileDeleted?.invoke()
        }
    }

    open fun startWatching() {
        fileObserver?.startWatching()
    }

    open fun stopWatching() {
        fileObserver?.stopWatching()
    }
}