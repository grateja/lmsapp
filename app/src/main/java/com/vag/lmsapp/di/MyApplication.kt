package com.vag.lmsapp.di

import android.app.Application
import com.vag.lmsapp.util.Constants
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val mediaDir = File(filesDir, Constants.PICTURES_DIR)
        if(!mediaDir.exists()) {
            mediaDir.mkdirs()
        }
    }
}