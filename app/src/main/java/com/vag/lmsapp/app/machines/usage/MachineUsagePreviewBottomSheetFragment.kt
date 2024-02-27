package com.vag.lmsapp.app.machines.usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.vag.lmsapp.databinding.FragmentBottomSheetMachineUsagePreviewBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachineUsagePreviewBottomSheetFragment : BaseModalFragment() {
    private val viewModel: MachineUsageViewModel by activityViewModels()
    private lateinit var binding: FragmentBottomSheetMachineUsagePreviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetMachineUsagePreviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}