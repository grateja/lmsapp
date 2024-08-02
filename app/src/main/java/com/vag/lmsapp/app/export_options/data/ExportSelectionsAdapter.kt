package com.vag.lmsapp.app.export_options.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.databinding.RecyclerItemCheckboxBinding
import com.vag.lmsapp.services.ExcelExportService
import com.vag.lmsapp.util.EnumCheckboxState

class ExportSelectionsAdapter: RecyclerView.Adapter<ExportSelectionsAdapter.ViewHolder>() {
    var selectionChanged: ((ArrayList<Selections>, state: EnumCheckboxState) -> Unit)? = null
    inner class ViewHolder(private val binding: RecyclerItemCheckboxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Selections) {
            binding.viewModel = model
            binding.checkbox.setOnClickListener {
                selectionChanged?.invoke(getSelectedItems(), state())
            }
        }
    }

    private fun getSelectedItems(): ArrayList<Selections> {
        return ArrayList(_items.filter { it.selected })
    }

    private fun state() : EnumCheckboxState {
        val totalItems = _items.size
        val selectedItems = _items.count { it.selected }

        return when (selectedItems) {
            0 -> EnumCheckboxState.UNCHECKED
            totalItems -> EnumCheckboxState.CHECKED
            else -> EnumCheckboxState.INDETERMINATE
        }
    }

    private val _items by lazy {
        arrayListOf(
            Selections(ExcelExportService.INCLUDES_NEW_CUSTOMERS, "New Customers"),
            Selections(ExcelExportService.INCLUDES_JOB_ORDER, "Job Orders"),
            Selections(ExcelExportService.INCLUDES_WASH_DRY_SERVICES, "Wash and Dry services"),
            Selections(ExcelExportService.INCLUDES_PRODUCTS_CHEMICALS, "Products and chemicals"),
            Selections(ExcelExportService.INCLUDES_EXTRAS, "Extra services"),
            Selections(ExcelExportService.INCLUDES_MACHINE_USAGES, "Machine usage history"),
            Selections(ExcelExportService.INCLUDES_DELIVERY_CHARGES, "Pick up & Deliveries"),
            Selections(ExcelExportService.INCLUDES_EXPENSES, "Expenses"),
            Selections(ExcelExportService.INCLUDES_UNPAID_JOB_ORDERS, "Unpaid job orders"),
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = _items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = _items[position]
        holder.bind(r)
    }

    fun updateCount(key: String, count: Int) {
        _items.find { it.key == key }?.apply {
            this.count = count
            this.selected = count > 0
            notifyItemChanged(_items.indexOf(this))
        }
        selectionChanged?.invoke(getSelectedItems(), state())
    }

    fun toggle() {
        val allSelected = _items.all { it.selected }
        _items.forEachIndexed { index, it ->
            if(it.selected != !allSelected) {
                it.selected = !allSelected
                notifyItemChanged(index)
            }
        }
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        val state = when {
            _items.all { it.selected } -> EnumCheckboxState.CHECKED
            _items.none { it.selected } -> EnumCheckboxState.UNCHECKED
            else -> EnumCheckboxState.INDETERMINATE
        }
        selectionChanged?.invoke(getSelectedItems(), state)
    }
}