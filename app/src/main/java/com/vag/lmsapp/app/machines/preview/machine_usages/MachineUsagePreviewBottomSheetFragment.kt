package com.vag.lmsapp.app.machines.preview.machine_usages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vag.lmsapp.databinding.FragmentBottomSheetMachineUsagePreviewBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails

class MachineUsagePreviewBottomSheetFragment : ModalFragment<EntityMachineUsageDetails>() {
    private lateinit var binding: FragmentBottomSheetMachineUsagePreviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetMachineUsagePreviewBinding.inflate(
            inflater, container, false
        )

        arguments?.getParcelable<EntityMachineUsageDetails>(PAYLOAD)?.let {
            binding.viewModel = it
        }

        return binding.root
    }
    companion object {
        fun newInstance(model: EntityMachineUsageDetails): MachineUsagePreviewBottomSheetFragment {
            return MachineUsagePreviewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, model)
                }
            }
        }
    }
}