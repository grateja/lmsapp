package com.vag.lmsapp.app.machines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.machines.addedit.MachinesAddEditActivity
import com.vag.lmsapp.databinding.ActivityMachinesBinding
import com.vag.lmsapp.util.Constants
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachinesBinding
    private val viewModel: MachinesViewModel by viewModels()
    private val adapter = Adapter<MachineListItem>(R.layout.recycler_item_machine_details)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_machines)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)

        binding.recyclerMachines.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
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
                putExtra(MachinesAddEditActivity.MACHINE_TYPE_EXTRA, it.machine.machineType?.id)
            }
            startActivity(intent)
        }
    }

    private fun subscribeListeners() {
        viewModel.machines.observe(this, Observer {
            adapter.setData(it)
        })
    }
}