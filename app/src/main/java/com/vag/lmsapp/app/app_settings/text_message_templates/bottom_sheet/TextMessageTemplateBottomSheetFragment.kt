package com.vag.lmsapp.app.app_settings.text_message_templates.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.text_message_templates.TextMessageTemplateListViewModel
import com.vag.lmsapp.databinding.FragmentTextMessageTemplateBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextMessageTemplateBottomSheetFragment : ModalFragment<String>() {
    private lateinit var binding: FragmentTextMessageTemplateBottomSheetBinding
    private val viewModel: TextMessageTemplateListViewModel by viewModels()
    private val adapter = Adapter<EntityTextMessageTemplate>(R.layout.recycler_item_text_message_template_full)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextMessageTemplateBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recycler.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }

        adapter.onItemClick = {
            onOk?.invoke(it.message)
            dismiss()
        }
    }

    private fun subscribeListeners() {
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
    }
}