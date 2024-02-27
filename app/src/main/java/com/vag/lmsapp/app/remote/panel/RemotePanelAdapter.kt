package com.vag.lmsapp.app.remote.panel

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.BR
import com.vag.lmsapp.R
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.databinding.RecyclerItemMachineTileBinding

class RemotePanelAdapter : RecyclerView.Adapter<RemotePanelAdapter.ViewHolder>() {
    var handler = Handler(Looper.getMainLooper())
    var list = emptyList<MachineListItem>()
    var onItemClick: ((MachineListItem) -> Unit) ? = null
    var onOptionClick: ((MachineListItem) -> Unit) ? = null
//    private val itemRunnables = mutableMapOf<MachineListItem, Runnable>()
    class ViewHolder(private val binding: RecyclerItemMachineTileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MachineListItem) {
            binding.setVariable(BR.viewModel, model)

            val context = binding.root.context

            if(model.machine.serviceActivationId != null) {
                binding.machineTile.strokeColor = context.getColor(R.color.purple_200)
                binding.machineTile.setCardBackgroundColor(context.getColor(R.color.darker_background))
            } else if(model.machine.activationRef?.running() == true) {
                binding.machineTile.strokeColor = context.getColor(R.color.color_code_machines)
                binding.machineTile.setCardBackgroundColor(context.getColor(R.color.color_code_machines_highlight))
            } else {
                binding.machineTile.strokeColor = context.getColor(R.color.white)
                binding.machineTile.setCardBackgroundColor(context.getColor(R.color.white))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemMachineTileBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    private fun startTimer(item: MachineListItem) {
        val remainingSeconds = item.machine.activationRef?.remainingSeconds() ?: 0
        val remainder = remainingSeconds % 60
        if(remainder > 0) {
            handler.postDelayed({
                startTimer(item)
            }, (remainder * 1000) + 1000)
        }
        notifyItemChanged(list.indexOf(item))
    }

    fun stopUpdatingTime() {
        // Remove all pending Runnables when the RecyclerView is being destroyed
        handler.removeCallbacksAndMessages(null)
        println("runnables removed")
    }

    fun startUpdatingTime() {
        list.forEach {
            startTimer(it)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        stopUpdatingTime()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.bind(r)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(r)
        }
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            onOptionClick?.invoke(r) != null
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(list: List<MachineListItem>) {
        this.list = list
        notifyDataSetChanged()
    }
}