package com.vag.lmsapp.util

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthActionDialogActivity.Companion.LAUNCH_CODE
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.model.EnumActionPermission

class AuthLauncherFragment(private val fragment: Fragment) {
    var onOk: ((LoginCredentials, String) -> Unit) ? = null
    var onCancel: ((String) -> Unit) ? = null
    private var active = false
    private var resultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val launchCode = result.data?.getStringExtra(LAUNCH_CODE) ?: ""
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

    fun launch(permissions: List<EnumActionPermission>, action: String, mandate: Boolean) {
        if(!active) {
            val intent = Intent(fragment.activity, AuthActionDialogActivity::class.java).apply {
                putExtra(LAUNCH_CODE, action)
                putExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, ArrayList(permissions))
                putExtra(AuthActionDialogActivity.MANDATE, mandate)
            }
            resultLauncher.launch(intent)
            active = true
        }
    }
}