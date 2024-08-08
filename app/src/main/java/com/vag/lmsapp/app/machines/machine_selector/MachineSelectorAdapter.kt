package com.vag.lmsapp.app.machines.machine_selector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.databinding.RecyclerItemMachineTileGridBinding
import java.util.UUID

class MachineSelectorAdapter: RecyclerView.Adapter<MachineSelectorAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: RecyclerItemMachineTileGridBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(machine: MachineListItem) {
            binding.viewModel = machine

            val context = binding.root.context

            if(machine.machine.id == _activeMachineId) {
                binding.machineTile.strokeColor = context.getColor(R.color.primary)
                binding.machineTile.setCardBackgroundColor(context.getColor(R.color.color_code_machines_highlight))
            } else {
                binding.machineTile.strokeColor = context.getColor(R.color.white)
                binding.machineTile.setCardBackgroundColor(context.getColor(R.color.white))
            }
        }
    }

    var onItemClick: ((MachineListItem?) -> Unit) ? = null

    private var _machines = mutableListOf<MachineListItem>()
    private var _activeMachineId: UUID? = null

    fun setData(machines: List<MachineListItem>) {
        _machines = machines.toMutableList()
        notifyDataSetChanged()
    }

    fun select(machineId: UUID?) {
        val active = _machines.find { it.machine.id == _activeMachineId }
        val next = _machines.find { it.machine.id == machineId }
        _activeMachineId = machineId

        _machines.indexOf(active).takeIf { it >= 0 }?.let {
            notifyItemChanged(it)
        }

        _machines.indexOf(next).takeIf { it >= 0 }?.let {
            notifyItemChanged(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemMachineTileGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _machines.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machine = _machines[position]
        holder.bind(machine)
        holder.itemView.setOnClickListener {
            select(machine.machine.id)
            onItemClick?.invoke(machine)
        }
    }
}