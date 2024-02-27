package com.vag.lmsapp.app.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.databinding.FragmentRequiredAuthPrivilegesBinding
import com.vag.lmsapp.fragments.BaseModalFragment

class RequiredAuthPrivilegesFragment : BaseModalFragment() {
    private val viewModel: AuthDialogViewModel by activityViewModels()
    private lateinit var binding: FragmentRequiredAuthPrivilegesBinding
    private val privilegeAdapter = Adapter<String>(R.layout.recycler_item_simple_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequiredAuthPrivilegesBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerPrivilege.adapter = privilegeAdapter

        viewModel.permissions.observe(viewLifecycleOwner, Observer {
            privilegeAdapter.setData(it.map { it.description })
        })

        return binding.root
    }

    companion object {
        var instance: RequiredAuthPrivilegesFragment? = null
        fun newInstance() : RequiredAuthPrivilegesFragment {
            if (instance == null || instance?.dismissed == true) {
                instance = RequiredAuthPrivilegesFragment()
            }
            return instance as RequiredAuthPrivilegesFragment
        }
    }
}