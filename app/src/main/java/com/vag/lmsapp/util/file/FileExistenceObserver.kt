package com.vag.lmsapp.util.file

import android.os.FileObserver

class FileExistenceObserver(private val filePath: String) : FileObserver(filePath, FileObserver.CLOSE_WRITE or FileObserver.DELETE) {

    var onFileCreated: (() -> Unit)? = null
    var onFileDeleted: (() -> Unit)? = null

    override fun onEvent(event: Int, path: String?) {
        when (event) {
            FileObserver.CREATE -> onFileCreated?.invoke()
            FileObserver.DELETE -> onFileDeleted?.invoke()
        }
    }
}
