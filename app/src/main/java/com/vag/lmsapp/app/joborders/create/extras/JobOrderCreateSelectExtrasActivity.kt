package com.vag.lmsapp.app.joborders.create.extras

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.shared_ui.CreateJobOrderModifyQuantityBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectExtrasBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectExtrasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderCreateSelectExtrasBinding

    private val viewModel: AvailableExtrasViewModel by viewModels()

    private lateinit var modifyQuantityDialog: CreateJobOrderModifyQuantityBottomSheetFragment

    private val extrasAdapter = AvailableExtrasAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_extras)
        binding.lifecycleOwner = this

        binding.recyclerAvailableExtras.adapter = extrasAdapter

        subscribeEvents()

//        val extras = intent.getParcelableArrayListExtra<MenuExtrasItem?>("extras")
//        println("preset")
//        println(extras?.size)

//        viewModel.setPreSelectedServices(intent.getParcelableArrayListExtra<MenuExtrasItem?>("extras")?.toList())
        intent.getParcelableExtra<MenuExtrasItem>(JobOrderCreateActivity.ITEM_PRESET_EXTRA)?.let {
            itemClick(it)
        }
    }
    private fun subscribeEvents() {
        extrasAdapter.onItemClick = { itemClick(it) }
        binding.buttonOk.setOnClickListener {
            viewModel.prepareSubmit()
        }
        binding.buttonCancel.setOnClickListener {
            finish()
        }
        viewModel.availableExtras.observe(this, Observer {
            extrasAdapter.setData(it)
            viewModel.setPreSelectedServices(
                intent.getParcelableArrayListExtra<MenuExtrasItem?>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.toList()
            )
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is AvailableExtrasViewModel.DataState.UpdateService -> {
                    extrasAdapter.updateItem(it.extrasItem)
                    viewModel.resetState()
                }
                is AvailableExtrasViewModel.DataState.Submit -> {
                    submit(it.selectedItems)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun itemClick(extrasItem: MenuExtrasItem) {
        requestModifyQuantity(
            QuantityModel(
                extrasItem.extrasRefId,
                extrasItem.name,
                extrasItem.quantity.toFloat(),
                QuantityModel.TYPE_EXTRAS
            )
        )
    }

    private fun requestModifyQuantity(quantityModel: QuantityModel) {
        modifyQuantityDialog = CreateJobOrderModifyQuantityBottomSheetFragment.getInstance(quantityModel).apply {
            onOk = {
                if (it.type == QuantityModel.TYPE_EXTRAS) {
                    viewModel.putService(it)
                }
            }
            onItemRemove = {
                viewModel.removeService(it)
            }
        }
        modifyQuantityDialog.show(supportFragmentManager, this.toString())
    }

    private fun submit(list: List<MenuExtrasItem>) {
        setResult(RESULT_OK, Intent().apply {
            action = intent.action
            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(list))
        })
        finish()
    }
}