package com.vag.lmsapp.app.machines.options

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.app.machines.addedit.MachinesAddEditActivity
import com.vag.lmsapp.app.machines.usage.MachineUsageActivity
import com.vag.lmsapp.databinding.FragmentBottomSheetMachineOptionsBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.MACHINE_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BottomSheetMachineOptionsFragment : ModalFragment<MachineListItem>() {
    private lateinit var binding: FragmentBottomSheetMachineOptionsBinding
    private val viewModel: MachineOptionsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetMachineOptionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        arguments?.getString(Constants.MACHINE_ID)?.toUUID()?.let {
            viewModel.setMachineId(it)
        }

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    private fun subscribeEvents() {
        binding.inclUsageHistory.card.setOnClickListener {
            viewModel.openMachineHistory()
        }
        binding.inclConfigure.card.setOnClickListener {
            viewModel.openMachineConfiguration()
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            val intent = when(it) {
                is MachineOptionsViewModel.NavigationState.OpenMachineHistory -> {
                    Intent(context, MachineUsageActivity::class.java).apply {
                        putExtra(MACHINE_ID, it.machineId.toString())
                    }
                }
                is MachineOptionsViewModel.NavigationState.OpenMachineConfiguration -> {
                    Intent(context, MachinesAddEditActivity::class.java).apply {
                        putExtra(MACHINE_ID, it.machineId.toString())
                    }
                }
                else -> {
                    null
                }
            }
            intent?.let { _intent ->
                startActivity(_intent)
                viewModel.resetState()
            }
        })
    }

    companion object {
        var instance: BottomSheetMachineOptionsFragment? = null
        fun getInstance(machineId: UUID): BottomSheetMachineOptionsFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = BottomSheetMachineOptionsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.MACHINE_ID, machineId.toString())
                    }
                }
            }
            return instance as BottomSheetMachineOptionsFragment
        }
    }
}