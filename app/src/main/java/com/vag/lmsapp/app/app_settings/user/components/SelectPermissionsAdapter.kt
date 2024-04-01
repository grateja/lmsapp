package com.vag.lmsapp.app.app_settings.user.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.RecyclerItemSelectUserPermissionBinding
import com.vag.lmsapp.model.EnumActionPermission

class SelectPermissionsAdapter : RecyclerView.Adapter<SelectPermissionsAdapter.ViewHolder>() {
    var onSelectionChanged: ((EnumActionPermission) -> Unit)? = null
    data class UserPermissionAdapterWrapper(
        var selected: Boolean,
        var permission: EnumActionPermission
    )
    inner class ViewHolder(val binding: RecyclerItemSelectUserPermissionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: UserPermissionAdapterWrapper) {
            binding.viewModel = model
            binding.checkbox.setOnClickListener {
                onSelectionChanged?.invoke(model.permission)
            }
        }
    }

    val permissions = EnumActionPermission.entries.map {
        UserPermissionAdapterWrapper(false, it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemSelectUserPermissionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_item_select_user_permission,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return permissions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(permissions[position])
    }

    fun setSelected(selectedPermissions: List<EnumActionPermission>) {
        permissions.forEach { wrapper ->
            val shouldBeSelected = selectedPermissions.any { it == wrapper.permission }
            if (wrapper.selected != shouldBeSelected) {
                wrapper.selected = shouldBeSelected
                notifyItemChanged(permissions.indexOf(wrapper))
            }
        }
    }
}