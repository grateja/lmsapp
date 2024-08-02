package com.vag.lmsapp.util.file

import android.os.FileObserver
import androidx.lifecycle.LiveData
import java.io.File

class FileExistenceLiveData(private val filePath: String, private val fileName: String) : LiveData<File>() {

    private val fileObserver = FileObserverApiWrapper(filePath, FileObserver.CREATE or FileObserver.DELETE)

    override fun onActive() {
        super.onActive()
        fileObserver.onFileCreated = { checkFile() }
        fileObserver.onFileDeleted = { checkFile() }
        fileObserver.startWatching()
        checkFile()
    }

    private fun checkFile() {
        postValue(File(filePath, fileName))
    }

    override fun onInactive() {
        super.onInactive()
        fileObserver.stopWatching()
    }
}
