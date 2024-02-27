package com.vag.lmsapp.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ActivityLauncher(activity: AppCompatActivity) {
    var onOk: ((ActivityResult) -> Unit) ? = null
    var onCancel: (() -> Unit) ? = null
    private var active = false
    private var resultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            println("ACTIVITY ACTION")
            println(result?.data?.action)
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                onOk?.invoke(result)
            } else if(result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                onCancel?.invoke()
            }
            active = false
        }

    fun launch(intent: Intent) {
        if(!active) {
            resultLauncher.launch(intent)
            active = true
        }
    }
}