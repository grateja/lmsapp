package com.vag.lmsapp.app.machines.preview.machine_usages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.printer.PrinterPreviewActivity
import com.vag.lmsapp.databinding.FragmentBottomSheetMachineUsagePreviewBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.util.DataState

class MachineUsagePreviewBottomSheetFragment : ModalFragment<EntityMachineUsageDetails>() {
    private lateinit var binding: FragmentBottomSheetMachineUsagePreviewBinding
    private val viewModel: MachineUsagePreviewViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetMachineUsagePreviewBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.getParcelable<EntityMachineUsageDetails>(PAYLOAD)?.let {
            viewModel.setMachineUsage(it)
        }

        binding.cardButtonPrint.setOnClickListener {
            viewModel.initiatePrint()
        }

        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.OpenActivity -> {
                    if(it.activityClass == PrinterPreviewActivity::class.java) {
                        startActivity(
                            Intent(context, PrinterPreviewActivity::class.java).apply {
                                it.extras?.let { extras -> putExtras(extras) }
                            }
                        )
                    }
                    viewModel.resetState()
                }
                else -> {}
            }
        })

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