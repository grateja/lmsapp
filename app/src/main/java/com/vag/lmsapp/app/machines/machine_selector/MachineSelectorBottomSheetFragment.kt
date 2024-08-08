package com.vag.lmsapp.app.machines.machine_selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.databinding.FragmentMachineSelectorBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.util.Constants.Companion.MACHINE_ID
import com.vag.lmsapp.util.calculateSpanCount
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MachineSelectorBottomSheetFragment : ModalFragment<UUID?>() {
    private lateinit var binding: FragmentMachineSelectorBottomSheetBinding
    private val viewModel: MachineSelectorViewModel by activityViewModels()
    private val adapter = MachineSelectorAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMachineSelectorBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = GridLayoutManager(context, spanCount)

        subscribeEvents()
        subscribeListeners()

        val machineId = arguments?.getString(MACHINE_ID).toUUID()
        viewModel.setActiveMachine(machineId)
        return binding.root
    }

    private val spanCount: Int by lazy {
        requireContext().calculateSpanCount(150.dp, 16.dp)
    }

    private fun subscribeEvents() {
        binding.tabMachineType.cardRegularWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardRegularDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.DRY)
        }
        binding.tabMachineType.cardTitanWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardTitanDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.DRY)
        }
        adapter.onItemClick = {
            onOk?.invoke(it?.machine?.id)
            dismiss()
        }
    }

    private fun subscribeListeners() {
        viewModel.machines.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
        viewModel.machineId.observe(viewLifecycleOwner, Observer {
            adapter.select(it)
        })
    }

    companion object {
        fun newInstance(machineId: UUID?) : MachineSelectorBottomSheetFragment {
            return MachineSelectorBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(MACHINE_ID, machineId.toString())
                }
            }
        }
    }
}