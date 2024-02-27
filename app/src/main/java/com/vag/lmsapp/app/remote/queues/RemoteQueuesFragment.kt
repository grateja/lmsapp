package com.vag.lmsapp.app.remote.queues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.remote.shared_ui.RemoteActivationViewModel
import com.vag.lmsapp.databinding.FragmentRemoteQueuesBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.room.entities.EntityAvailableService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteQueuesFragment : BaseModalFragment() {
    private lateinit var binding: FragmentRemoteQueuesBinding
    private val viewModel: RemoteActivationViewModel by activityViewModels()

    private val serviceQueuesAdapter = Adapter<EntityAvailableService>(R.layout.recycler_item_queue_service)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRemoteQueuesBinding.inflate(inflater, container, false)
        binding.recyclerAvailableServices.adapter = serviceQueuesAdapter
        binding.viewModel = viewModel

        viewModel.availableServices.observe(viewLifecycleOwner, Observer {
            serviceQueuesAdapter.setData(it)
        })

        serviceQueuesAdapter.onItemClick = {
            viewModel.selectService(it)
        }

        return binding.root
    }

    companion object {
        var instance: RemoteQueuesFragment? = null
        fun newInstance() : RemoteQueuesFragment {
            if(instance == null || instance?.dismissed == true) {
                instance = RemoteQueuesFragment()
            }
            return instance as RemoteQueuesFragment
        }
    }
}