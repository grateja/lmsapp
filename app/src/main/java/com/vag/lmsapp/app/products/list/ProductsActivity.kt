package com.vag.lmsapp.app.products.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.app.products.edit.ProductAddEditActivity
import com.vag.lmsapp.app.products.list.advanced_filter.ProductListAdvancedFilterBottomSheetFragment
import com.vag.lmsapp.app.products.preview.ProductPreviewActivity
import com.vag.lmsapp.databinding.ActivityProductsBinding
import com.vag.lmsapp.util.*
import com.vag.lmsapp.util.Constants.Companion.PRODUCT_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ProductsActivity : FilterActivity() {
    private lateinit var binding: ActivityProductsBinding
    private val viewModel: ProductsViewModel by viewModels()
    private val adapter = Adapter<ProductItemFull>(R.layout.recycler_item_products_full)
    override var enableAdvancedFilter = true

    override var filterHint = "Search Products"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)

        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recycler.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        binding.buttonCreateNew.setOnClickListener {
            addProduct()
        }
        adapter.onItemClick = {
            openPreview(it.product.id)
        }
//        addEditLauncher.onOk = {
//            viewModel.filter(true)
//        }
    }

    private fun addProduct() {
        val intent = Intent(this, ProductAddEditActivity::class.java)
        addEditLauncher.launch(intent)
    }

    private fun openPreview(productId: UUID) {
//        ProductPreviewBottomSheetFragment.newInstance(productId).show(supportFragmentManager, null)
        val intent = Intent(this, ProductPreviewActivity::class.java).apply {
            putExtra(PRODUCT_ID, productId.toString())
        }
        startActivity(intent)
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

                is FilterState.ShowAdvancedFilter -> {
                    ProductListAdvancedFilterBottomSheetFragment.newInstance(it.advancedFilter).apply{
                        onOk = {
                            viewModel.setFilterParams(it)
                            viewModel.filter(true)
                        }
                    }.show(supportFragmentManager, null)
                    viewModel.clearState()
                }


                else -> {}
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    override fun onAdvancedSearchClick() {
        viewModel.showAdvancedFilter()
    }
}