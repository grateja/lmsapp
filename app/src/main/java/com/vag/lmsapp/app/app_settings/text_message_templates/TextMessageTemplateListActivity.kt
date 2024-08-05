package com.vag.lmsapp.app.app_settings.text_message_templates

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.text_message_templates.add_edit.TextMessageTemplateAddEditActivity
import com.vag.lmsapp.databinding.ActivityTextMessageTemplateListBinding
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class TextMessageTemplateListActivity : FilterActivity() {
    private val viewModel: TextMessageTemplateListViewModel by viewModels()
    private lateinit var binding: ActivityTextMessageTemplateListBinding

    private val adapter = Adapter<EntityTextMessageTemplate>(R.layout.recycler_item_text_message_template_full)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_message_template_list)
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = adapter

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        subscribeListeners()
        subscribeEvents()
    }

    override var filterHint: String = "Search title or body template"
    override var enableAdvancedFilter: Boolean = false

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    private fun subscribeListeners() {
        viewModel.filterState.observe(this, Observer {
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

    private fun subscribeEvents() {
        binding.buttonCreateNew.setOnClickListener {
            openAddEdit(null)
        }
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        adapter.onItemClick = {
            openAddEdit(it.id)
        }
    }

    private fun openAddEdit(id: UUID?) {
        val intent = Intent(this, TextMessageTemplateAddEditActivity::class.java).apply {
            putExtra(ENTITY_ID, id.toString())
        }
        addEditLauncher.launch(intent)
    }
}