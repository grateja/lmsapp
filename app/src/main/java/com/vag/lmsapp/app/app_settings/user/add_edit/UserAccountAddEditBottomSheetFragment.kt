package com.vag.lmsapp.app.app_settings.user.add_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.app_settings.user.components.SelectPermissionsAdapter
import com.vag.lmsapp.databinding.FragmentUserAccountAddEditBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class UserAccountAddEditBottomSheetFragment : BaseModalFragment() {
    override var fullHeight: Boolean = true
    private val viewModel: UserAccountAddEditViewModel by viewModels()
    private lateinit var binding: FragmentUserAccountAddEditBottomSheetBinding
    val permissions = SelectPermissionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountAddEditBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewPermissions.adapter = permissions
        binding.controls.viewModel = viewModel

        subscribeEvents()
        subscribeListeners()

        val userId = arguments?.getString(USER_ID).toUUID()
        viewModel.get(userId)

        arguments?.getString(AUTH_ID).toUUID()?.let {
            viewModel.setAuthId(it)
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.controls.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.cardRoleOwner.setOnClickListener {
            viewModel.setRole(Role.OWNER)
        }
        binding.cardRoleStaff.setOnClickListener {
            viewModel.setRole(Role.STAFF)
        }
        permissions.onSelectionChanged = {
            viewModel.selectPermission(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                    dismiss()
                }

                is DataState.Invalidate -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
        viewModel.permissions.observe(viewLifecycleOwner, Observer {
            permissions.setSelected(it)
        })
        viewModel.authorizedUser.observe(viewLifecycleOwner, Observer {
            println("authorizer")
            println(it.user)
        })
    }

    companion object {
        const val CAN_CREATE_OWNER = "can_create_owner"

        fun newInstance(userId: UUID?, authId: UUID): UserAccountAddEditBottomSheetFragment{
            val args = Bundle().apply {
                putString(USER_ID, userId.toString())
                putString(AUTH_ID, authId.toString())
            }

            val fragment = UserAccountAddEditBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}