package com.vag.lmsapp.util

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class FragmentLauncher(fragment: Fragment) {
    var onOk: ((Intent?) -> Unit) ? = null
    var onCancel: (() -> Unit) ? = null
    private var active = false
    private var resultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                onOk?.invoke(result.data)
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