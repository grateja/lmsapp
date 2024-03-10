//package com.vag.lmsapp.app.remote.customer
//
//import android.os.Bundle
//import android.view.*
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.Observer
//import com.vag.lmsapp.R
//import com.vag.lmsapp.adapters.Adapter
//import com.vag.lmsapp.app.remote.shared_ui.RemoteActivationViewModel
//import com.vag.lmsapp.databinding.FragmentRemoteCustomerBinding
//import com.vag.lmsapp.fragments.BaseModalFragment
//import com.vag.lmsapp.room.entities.EntityCustomerQueueService
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RemoteCustomerFragment : BaseModalFragment() {
//
//    private lateinit var binding: FragmentRemoteCustomerBinding
//    private val viewModel: RemoteActivationViewModel by activityViewModels()
//
//    private val customerQueuesAdapter = Adapter<EntityCustomerQueueService>(R.layout.recycler_item_queue_customer)
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentRemoteCustomerBinding.inflate(inflater, container, false)
//        binding.lifecycleOwner = viewLifecycleOwner
//        binding.viewModel = viewModel
//        binding.recyclerCustomerQueues.adapter = customerQueuesAdapter
//        binding.buttonClose.setOnClickListener {
//            dismiss()
//        }
//
//        viewModel.customerQueues.observe(viewLifecycleOwner, Observer { list ->
//            list?.let {
//                customerQueuesAdapter.setData(it)
//                println("queues loaded")
//            }
//        })
//
//        customerQueuesAdapter.onItemClick = {
//            viewModel.selectCustomer(it)
//        }
//
//        viewModel.machine.observe(viewLifecycleOwner, Observer {
//            if(it?.activationRef?.running() == true) {
//
//            }
//        })
//
//        return binding.root
//    }
//
////    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////        val dialog = Dialog(requireContext(), R.style.FullHeightDialog)
////        val window: Window? = dialog.window
////        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
////        window?.setGravity(Gravity.BOTTOM)
////
////        return dialog
////    }
//
//    companion object {
//        var instance: RemoteCustomerFragment? = null
//        fun newInstance() : RemoteCustomerFragment {
//            if(instance == null || instance?.dismissed == true) {
//                instance = RemoteCustomerFragment()
//            }
//            return instance as RemoteCustomerFragment
//        }
//    }
//}