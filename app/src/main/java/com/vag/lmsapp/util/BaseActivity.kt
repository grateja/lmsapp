package com.vag.lmsapp.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected var backPressed = false

//    protected lateinit var authLauncher: ActivityLauncher

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

//    override fun onStart() {
//        super.onStart()
//        findViewById<Button>(R.id.buttonSave)?.setOnClickListener {
//            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//                action = ACTION_SAVE
//            }
//            authLauncher.launch(intent)
//        }
//
//        findViewById<Button>(R.id.buttonDelete)?.setOnClickListener {
//            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//                action = ACTION_DELETE
//            }
//            authLauncher.launch(intent)
//        }
//
//        authLauncher.onOk = {
//            val loginCredentials = it.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)
//            if(it.data?.action == ACTION_SAVE) {
//                this.save(loginCredentials)
//            } else if(it.data?.action == ACTION_DELETE) {
//                AlertDialog.Builder(this).apply {
//                    setTitle("Delete this item")
//                    setMessage("Are you sure you want to proceed?")
//                    setPositiveButton("Yes") { _, _ ->
//                        confirmDelete(loginCredentials)
//                    }
//                    create()
//                }.show()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        authLauncher = ActivityLauncher(this)
//
//        intent.getStringExtra(ENTITY_ID).toUUID().let {
//            this.get(it)
//        }
//    }

//    protected open fun get(id: UUID?) { }
//    protected open fun save(loginCredentials: LoginCredentials?) { }
//    protected open fun confirmDelete(loginCredentials: LoginCredentials?) { }
}