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
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.machines.addedit.MachinesAddEditActivity
import com.vag.lmsapp.databinding.ActivityMachinesBinding
import com.vag.lmsapp.util.Constants
import com.google.android.material.tabs.TabLayout
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachinesBinding
    private val viewModel: MachinesViewModel by viewModels()
    private val adapter = MachinesAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_details)

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

//        binding.tabMachineType.tabMachineType.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewModel.setMachineType(tab?.text.toString())
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })
        adapter.onItemClick = {
            val intent = Intent(this, MachinesAddEditActivity::class.java).apply {
                putExtra(Constants.MACHINE_ID_EXTRA, it.machine.id.toString())
                putExtra(MachinesAddEditActivity.MACHINE_TYPE_FILTER, MachineTypeFilter(
                    it.machine.machineType,
                    it.machine.serviceType
                ))
            }
            startActivity(intent)
        }
        adapter.onPositionChanged = {
            viewModel.setNewPositions(it)
            println("rearranged")
            println(it.map { it.machine.stackOrder })
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
    }
}