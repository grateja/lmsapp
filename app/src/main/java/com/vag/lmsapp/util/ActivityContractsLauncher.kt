package com.vag.lmsapp.util

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ActivityContractsLauncher(activity: AppCompatActivity) {
    var onOk: ((Map<String, Boolean>) -> Unit) ? = null
    var onCancel: (() -> Unit) ? = null
    private var resultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            onOk?.invoke(result)
//            if(result == AppCompatActivity.RESULT_OK) {
//                println("id from activity launcher")
//                println(result.data?.getStringExtra("id"))
//                onOk?.invoke(result)
//            } else if(result.resultCode == AppCompatActivity.RESULT_CANCELED) {
//                onCancel?.invoke()
//            }
        }

    fun launch(permissions: Array<String>) {
        return resultLauncher.launch(permissions)
    }
}