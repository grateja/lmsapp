package com.vag.lmsapp.app.machines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.app.machines.addedit.MachinesAddEditActivity
import com.vag.lmsapp.app.machines.preview.MachinePreviewActivity
import com.vag.lmsapp.databinding.ActivityMachinesBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.MACHINE_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachinesBinding
    private val viewModel: MachinesViewModel by viewModels()
    private val adapter = MachinesAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_details)
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { _, code ->
            viewModel.openCreateNew()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_machines)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)

        binding.recyclerMachines.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                adapter.moveItem(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No swipe action
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMachines)
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
            val intent = Intent(this, MachinePreviewActivity::class.java).apply {
                putExtra(MACHINE_ID, it.machine.id.toString())
            }
            startActivity(intent)
        }
        adapter.onPositionChanged = {
            viewModel.setNewPositions(it)
        }

        binding.buttonCreateNew.setOnClickListener {
            authLauncher.launch(listOf(EnumActionPermission.MODIFY_MACHINES), 1)
//            viewModel.openCreateNew()
        }

        binding.cardButtonConfirm.setOnClickListener {
            viewModel.save()
        }

        binding.cardButtonClose.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun subscribeListeners() {
        viewModel.machines.observe(this, Observer {
            adapter.setData(it)
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is MachinesViewModel.NavigationState.OpenCreateNew -> {
                    openCreateNew(it.machineTypeFilter)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun openCreateNew(machineTypeFilter: MachineTypeFilter) {
        val intent = Intent(this, MachinesAddEditActivity::class.java).apply {
            putExtra(MachinesAddEditActivity.MACHINE_TYPE_FILTER, machineTypeFilter)
        }
        startActivity(intent)
    }
}