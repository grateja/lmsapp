package com.vag.lmsapp.app

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class EndingActivity : AppCompatActivity() {
    private var doubleclick = false
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        if(doubleclick) {
//            super.onBackPressed()
            moveTaskToBack(true)
//            finish()
        }
        doubleclick = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleclick = false
        }, 2000)
    }
}