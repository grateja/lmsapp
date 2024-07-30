package com.vag.lmsapp.app.products.preview.inventory_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.databinding.FragmentInventoryLogBinding
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.util.FilterState

class InventoryLogFragment : Fragment() {
    private lateinit var binding: FragmentInventoryLogBinding
    private val viewModel: InventoryLogViewModel by activityViewModels()
    private val adapter = Adapter<EntityInventoryLogFull>(R.layout.recycler_item_inventory_log)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventoryLogBinding.inflate(
            inflater, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.recyclerInventoryLog.adapter = adapter

        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }

        viewModel.filterState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    viewModel.clearState()
                }
                else -> {}
            }
        })

        return binding.root
    }
}