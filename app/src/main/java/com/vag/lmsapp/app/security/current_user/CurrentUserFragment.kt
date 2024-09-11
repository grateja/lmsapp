package com.vag.lmsapp.app.security.current_user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.app_settings.user.preview.UserAccountPreviewActivity
import com.vag.lmsapp.databinding.FragmentCurrentUserBinding
import com.vag.lmsapp.util.AuthLauncherFragment
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID

class CurrentUserFragment : Fragment() {
    private val viewModel: CurrentUserViewModel by activityViewModels()
    private lateinit var binding: FragmentCurrentUserBinding
    private val authFragmentLauncher = AuthLauncherFragment(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentUserBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()
        return binding.root
    }

    private fun subscribeListeners() {

        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            println("current user")
            println(it?.user?.email)
        })

        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is CurrentUserViewModel.NavigationState.SwitchUser -> {
                    authFragmentLauncher.launch(listOf(), "Switch user", true)
                    viewModel.resetState()
                }

                is CurrentUserViewModel.NavigationState.OpenAccount -> {
                    val intent = Intent(context, UserAccountPreviewActivity::class.java).apply {
                        putExtra(USER_ID, it.userId.toString())
                        putExtra(AUTH_ID, it.userId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonSwitchUser.setOnClickListener {
            viewModel.switchUser()
        }

        binding.root.setOnClickListener {
            viewModel.openAccount()
        }
    }
}