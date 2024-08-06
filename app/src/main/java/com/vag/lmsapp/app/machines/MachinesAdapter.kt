package com.vag.lmsapp.app.machines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemMachineDetailsBinding

class MachinesAdapter: RecyclerView.Adapter<MachinesAdapter.ViewHolder>() {
    var onItemClick: ((MachineListItem) -> Unit)? = null
    var onPositionChanged: ((List<MachineListItem>) -> Unit)? = null
    inner class ViewHolder(val binding: RecyclerItemMachineDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(machine: MachineListItem) {
            binding.viewModel = machine
        }
    }

    private var _machines = mutableListOf<MachineListItem>()

    fun setData(machines: List<MachineListItem>) {
        _machines = machines.toMutableList()
        notifyDataSetChanged()
    }

    private fun updatePositions(fromPosition: Int, toPosition: Int) {
        val machineFrom = _machines[fromPosition]
        val machineTo = _machines[toPosition]
        val machineFromIndex = machineFrom.machine.stackOrder
        val machineToIndex = machineTo.machine.stackOrder

        _machines[fromPosition] = machineTo
        _machines[toPosition] = machineFrom

        machineFrom.machine.stackOrder = machineToIndex
        machineTo.machine.stackOrder = machineFromIndex

        onPositionChanged?.invoke(_machines)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
        updatePositions(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemMachineDetailsBinding.inflate(
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
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(machine)
        }
        holder.bind(machine)
    }
}