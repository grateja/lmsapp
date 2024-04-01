package com.vag.lmsapp.app.main_menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivitySubMenuBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubMenuActivity : AppCompatActivity() {
    companion object {
        const val SUB_MENU_ITEMS_EXTRA = "subMenuItems"
        const val SUB_MENU_TITLE_EXTRA = "title"
    }
    private val adapter = Adapter<MenuItem>(R.layout.recycler_item_sub_menu)
    private lateinit var binding: ActivitySubMenuBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val authLauncher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_menu)
        binding.lifecycleOwner = this
        binding.recyclerSubMenu.adapter = adapter

        intent.getParcelableArrayListExtra<MenuItem>(SUB_MENU_ITEMS_EXTRA)?.let {
            adapter.setData(it.toList())
        }
        intent.getStringExtra(SUB_MENU_TITLE_EXTRA)?.let {
            title = it
        }

        subscribeEvents()
    }

    private fun subscribeEvents() {
        mainViewModel.navigationState.observe(this, Observer {
            when(it) {
                is MainViewModel.NavigationState.OpenMenu -> {
                    openMenu(it.menuItem, it.loginCredentials)
                    mainViewModel.resetState()
                }
                is MainViewModel.NavigationState.RequestAuthentication -> {
                    requestPermission(it.menuItem)
                    mainViewModel.resetState()
                }

                else -> {}
            }
        })
        authLauncher.onOk = {
            when(it.data?.action) {
                AuthActionDialogActivity.AUTH_ACTION -> {
                    it.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
                        println("auth passed")
                        mainViewModel.permissionGranted(it)
                    }
                }
            }
        }
        adapter.onItemClick = {
            mainViewModel.openMenu(it)
        }
    }

    private fun requestPermission(menuItem: MenuItem) {
        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
            action = AuthActionDialogActivity.AUTH_ACTION
            menuItem.permissions?.let {
                putParcelableArrayListExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, ArrayList(it))
            }
//            menuItem.roles?.let {
//                putParcelableArrayListExtra(AuthActionDialogActivity.ROLES_EXTRA, ArrayList(it))
//            }
        }
        authLauncher.launch(intent)
    }

    private fun openMenu(menuItem: MenuItem, loginCredentials: LoginCredentials?) {
        val intent = if(menuItem.menuItems != null)
            Intent(this, SubMenuActivity::class.java).apply {
                putParcelableArrayListExtra(SUB_MENU_ITEMS_EXTRA, ArrayList(menuItem.menuItems))
                putExtra(SUB_MENU_TITLE_EXTRA, menuItem.text)
                println("login credentials")
                println(loginCredentials)
            }
        else
            Intent(this, menuItem.activityClass)
        intent.putExtra(AUTH_ID, loginCredentials?.userId.toString())

        startActivity(intent)
    }
}