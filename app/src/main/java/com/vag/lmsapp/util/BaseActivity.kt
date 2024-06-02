package com.vag.lmsapp.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected var backPressed = false

    open fun confirmExit(promptPass: Boolean) {
        if(promptPass) {
            finish()
        } else if(!backPressed) {
            Toast.makeText(this, "Press back again to revert changes", Toast.LENGTH_LONG).show()
            backPressed = true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                backPressed = false
            }, 2000)
        } else {
            finish()
        }
    }
}