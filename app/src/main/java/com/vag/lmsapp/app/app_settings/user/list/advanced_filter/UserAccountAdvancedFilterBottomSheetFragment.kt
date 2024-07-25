package com.vag.lmsapp.app.app_settings.user.list.advanced_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.databinding.FragmentUserAccountAdvancedFilterBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.DataState

class UserAccountAdvancedFilterBottomSheetFragment : ModalFragment<UserAccountAdvancedFilter>() {
    private lateinit var binding: FragmentUserAccountAdvancedFilterBottomSheetBinding
    private val viewModel: UserAccountAdvancedFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountAdvancedFilterBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<UserAccountAdvancedFilter>(PAYLOAD).let {
            viewModel.setInitialFilters(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Submit -> {
                    onOk?.invoke(it.data!!)
                    dismiss()
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonApply.setOnClickListener {
            viewModel.submit()
        }
    }

    companion object {
        fun newInstance(model: UserAccountAdvancedFilter): UserAccountAdvancedFilterBottomSheetFragment {
            return UserAccountAdvancedFilterBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, model)
                }
            }
        }
    }
}