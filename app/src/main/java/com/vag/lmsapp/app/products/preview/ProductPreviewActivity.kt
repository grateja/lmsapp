package com.vag.lmsapp.app.products.preview

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
import com.vag.lmsapp.app.products.add_stock.ProductAddStockBottomSheetFragment
import com.vag.lmsapp.app.products.edit.ProductAddEditActivity
import com.vag.lmsapp.app.products.preview.inventory_in.InventoryLogViewModel
import com.vag.lmsapp.databinding.ActivityProductPreviewBinding
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.util.Constants.Companion.PRODUCT_ID
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ProductPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductPreviewBinding
    private val viewModel: ProductPreviewViewModel by viewModels()
    private val inventoryViewModel: InventoryLogViewModel by viewModels()

//    private val fragmentAdapter = FragmentsAdapter(this)
    private val adapter = Adapter<EntityInventoryLogFull>(R.layout.recycler_item_inventory_log)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.inventoryLogViewModel = inventoryViewModel
        binding.recyclerInventoryLog.adapter = adapter
//        binding.productViewPager.adapter = fragmentAdapter

//        fragmentAdapter.setData(arrayListOf(
//            InventoryLogFragment(),
//            JobOrderProductFragment()
//        ))

//        TabLayoutMediator(binding.tabProductViewType, binding.productViewPager) {tab, position ->
//            tab.text = listOf("Stock in", "Stock out")[position]
//        }.attach()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        subscribeEvents()
        subscribeListeners()
    }

    override fun onResume() {
        super.onResume()
        intent.getStringExtra(PRODUCT_ID).toUUID()?.let {
            viewModel.setProductId(it)
            inventoryViewModel.setProductId(it)
            inventoryViewModel.filter(true)
        }
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            inventoryViewModel.loadMore()
        }
        binding.cardButtonEdit.setOnClickListener {
            viewModel.editProduct()
        }
        binding.cardButtonAddStock.setOnClickListener {
            viewModel.addStock()
        }
        binding.cardButtonDelete.setOnClickListener {
            showDeleteConfirmationDialog {
                viewModel.deleteProduct()
            }
        }
        binding.buttonCardHideToggle.setOnClickListener {
            viewModel.hideToggle()
        }
    }
    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is ProductPreviewViewModel.NavigationState.EditProduct -> {
                    openEditProduct(it.productId)
                    viewModel.resetState()
                    finish()
                }

                is ProductPreviewViewModel.NavigationState.AddStock -> {
                    openAddStock(it.productId)
                    viewModel.resetState()
                }

                is ProductPreviewViewModel.NavigationState.DeleteSuccess -> {
                    finish()
                    viewModel.resetState()
                }

                else -> {

                }
            }
        })
        inventoryViewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    inventoryViewModel.clearState()
                }
                else -> {}
            }
        })

    }


    private fun openEditProduct(productId: UUID) {
        val intent = Intent(this, ProductAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, productId.toString())
        }
        startActivity(intent)
    }

    private fun openAddStock(productId: UUID) {
        ProductAddStockBottomSheetFragment.newInstance(productId, null).apply {
            onOk = {
                inventoryViewModel.filter(true)
            }
        }.show(supportFragmentManager, null)
    }
}