package com.vag.lmsapp.util

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthActionDialogActivity.Companion.LAUNCH_CODE
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.model.EnumActionPermission

class AuthLauncherActivity(private val activity: AppCompatActivity) {
    var onOk: ((LoginCredentials, Int) -> Unit) ? = null
    var onCancel: ((Int?) -> Unit) ? = null
    private var active = false
    private var resultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val launchCode = result.data?.getIntExtra(LAUNCH_CODE, -1) ?: -1
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                val loginCredentials = result.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)
                onOk?.invoke(loginCredentials!!, launchCode)
                println("launch code")
                println(launchCode)
            } else if(result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                onCancel?.invoke(launchCode)
            }
            active = false
        }

    fun launch(permissions: List<EnumActionPermission>, launchCode: Int) {
        if(!active) {
            val intent = Intent(activity, AuthActionDialogActivity::class.java).apply {
                putExtra(LAUNCH_CODE, launchCode)
                putExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, ArrayList(permissions))
            }
            resultLauncher.launch(intent)
            active = true
        }
    }
}