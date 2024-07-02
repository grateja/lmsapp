package com.vag.lmsapp.app.products.preview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.products.add_stock.ProductAddStockBottomSheetFragment
import com.vag.lmsapp.app.products.edit.ProductAddEditActivity
import com.vag.lmsapp.databinding.FragmentProductPreviewBottomSheetBinding
import com.vag.lmsapp.fragments.ModalFragment
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ProductPreviewBottomSheetFragment : ModalFragment<UUID>() {
    override var fullHeight = true
    private lateinit var binding: FragmentProductPreviewBottomSheetBinding
    private val viewModel: ProductPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductPreviewBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscribeEvents()
        subscribeListeners()

        arguments?.getString(ID).toUUID()?.let {
            viewModel.setProductId(it)
        }

        return binding.root
    }

    private fun subscribeEvents() {
        binding.cardButtonEdit.setOnClickListener {
            viewModel.editProduct()
        }
        binding.cardButtonAddStock.setOnClickListener {
            viewModel.addStock()
        }
        binding.cardButtonDelete.setOnClickListener {
            context?.showDeleteConfirmationDialog {
                viewModel.deleteProduct()
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ProductPreviewViewModel.NavigationState.EditProduct -> {
                    openEditProduct(it.productId)
                    viewModel.resetState()
                    dismiss()
                }

                is ProductPreviewViewModel.NavigationState.AddStock -> {
                    openAddStock(it.productId)
                    viewModel.resetState()
                }

                is ProductPreviewViewModel.NavigationState.DeleteProduct -> {
                    viewModel.resetState()
                }

                else -> {

                }
            }
        })
    }

    private fun openEditProduct(productId: UUID) {
        val intent = Intent(context, ProductAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, productId.toString())
        }
        startActivity(intent)
    }

    private fun openAddStock(productId: UUID) {
        ProductAddStockBottomSheetFragment.newInstance(productId, null).show(parentFragmentManager, null)
    }

    companion object {
        fun newInstance(productId: UUID): ProductPreviewBottomSheetFragment {
            val args = Bundle().apply {
                putString(ID, productId.toString())
            }

            val fragment = ProductPreviewBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}