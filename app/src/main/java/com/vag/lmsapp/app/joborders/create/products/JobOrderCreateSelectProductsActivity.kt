package com.vag.lmsapp.app.joborders.create.products

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
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectProductsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderCreateSelectProductsBinding

    private val viewModel: AvailableProductsViewModel by viewModels()

//    private lateinit var modifyQuantityDialog: CreateJobOrderModifyQuantityBottomSheetFragment

    private val productsAdapter = AvailableProductsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_products)
        binding.lifecycleOwner = this

        binding.recyclerAvailableProducts.adapter = productsAdapter

        subscribeEvents()

        intent.getParcelableExtra<MenuProductItem>(JobOrderCreateActivity.ITEM_PRESET_EXTRA)?.let {
            itemClick(it)
        }
        viewModel.setPreselectedProducts(intent.getParcelableArrayListExtra<MenuProductItem?>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.toList())
    }

    private fun subscribeEvents() {
        productsAdapter.onItemClick = { itemClick(it) }
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.prepareSubmit()
        }
        binding.cardButtonClose.setOnClickListener {
            finish()
        }
        viewModel.availableProducts.observe(this, Observer {
            productsAdapter.setData(it)
//            viewModel.setPreselectedProducts(intent.getParcelableArrayListExtra<MenuProductItem?>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.toList())
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is AvailableProductsViewModel.DataState.UpdateProduct -> {
                    productsAdapter.updateItem(it.productItem)
                    viewModel.resetState()
                }
                is AvailableProductsViewModel.DataState.Submit -> {
                    submit(it.productItems)
                    viewModel.resetState()
                }
                is AvailableProductsViewModel.DataState.Invalidate -> {
                    productsAdapter.putError(it.inputValidation)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun itemClick(productItem: MenuProductItem) {
        requestModifyQuantity(
            QuantityModel(productItem.productRefId, productItem.name, productItem.quantity, QuantityModel.TYPE_PRODUCT)
        )
    }

    private fun requestModifyQuantity(quantityModel: QuantityModel) {
        CreateJobOrderModifyQuantityBottomSheetFragment.newInstance(quantityModel, QuantityModel.TYPE_PRODUCT).apply {
            onOk = {
                viewModel.putProduct(it)
            }
            onItemRemove = {
                viewModel.removeProduct(it)
            }
        }.show(supportFragmentManager, this.toString())
    }

    private fun submit(list: List<MenuProductItem>) {
        setResult(RESULT_OK, Intent().apply {
            action = intent.action
            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(list))
        })
        finish()
    }
}