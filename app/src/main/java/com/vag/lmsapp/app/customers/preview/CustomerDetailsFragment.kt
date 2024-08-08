package com.vag.lmsapp.app.customers.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.app_settings.text_message_templates.bottom_sheet.TextMessageTemplateBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.databinding.FragmentCustomerDetailsBinding
import com.vag.lmsapp.util.allowCopy
import com.vag.lmsapp.util.showMessageDialog

class CustomerDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCustomerDetailsBinding
    private val viewModel: CustomerPreviewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeListeners()
        subscribeEvents()

        binding.inclCustomerDetails.textAddress.allowCopy()
        binding.inclCustomerDetails.textEmail.allowCopy()
        binding.inclCustomerDetails.allowCallAndMessage = true

        return binding.root
    }

    private fun subscribeEvents() {
        binding.buttonCardEdit.setOnClickListener {
            viewModel.showCustomer()
        }

        binding.inclCustomerDetails.buttonOptionsContact.setOnClickListener {
            popupMenu.show()
        }

        binding.buttonCreateNewJobOrder.setOnClickListener {
            if(activity?.callingActivity?.className == JobOrderCreateActivity::class.java.name) {
                activity?.finish()
            } else {
                viewModel.prepareNewJobOrder()
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is CustomerPreviewViewModel.NavigationState.InvalidOperation -> {
                    context?.showMessageDialog("Cannot create new Job Order", it.message)
                    viewModel.resetState()
                }

                is CustomerPreviewViewModel.NavigationState.OpenDial -> {
                    openDial(it.contactNumber)
                    viewModel.resetState()
                }

                is CustomerPreviewViewModel.NavigationState.OpenMessage -> {
                    openMessage(it.contactNumber, it.message)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private val popupMenu: PopupMenu by lazy {
        PopupMenu(context, binding.inclCustomerDetails.buttonOptionsContact).apply {
            menuInflater.inflate(R.menu.contact_menu, menu)
            setOnMenuItemClickListener { item: MenuItem ->
                when(item.itemId) {
                    R.id.menu_contact_message -> {
                        TextMessageTemplateBottomSheetFragment().apply {
                            onOk = {
                                viewModel.showMessageWithTemplate(it)
                            }
                        }.show(parentFragmentManager, null)
                    }
                    R.id.menu_contact_call -> {
                        viewModel.prepareCall()
                    }
                }
                false
            }
        }
    }

//    private val template: TextMessageTemplateBottomSheetFragment by lazy {
//        TextMessageTemplateBottomSheetFragment().apply {
//            onOk = {
//                viewModel.showMessageWithTemplate(it)
//            }
//        }
//    }

    private fun openDial(contactNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$contactNumber")
        })
    }

    private fun openMessage(contactNumber: String, message: String) {
        startActivity(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$contactNumber")
            putExtra("sms_body", message)
        })
    }
}