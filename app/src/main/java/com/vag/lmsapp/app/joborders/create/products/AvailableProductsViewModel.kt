package com.vag.lmsapp.app.joborders.create.products

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AvailableProductsViewModel

@Inject constructor(
    private val repository: ProductRepository
)
: ViewModel() {
    private val _availableProducts = MutableLiveData<List<MenuProductItem>>()
    val availableProducts = MediatorLiveData<List<MenuProductItem>>().apply {
        addSource(_availableProducts) {
            value = it.filter {
                ((it.hidden && it.selected) || !it.hidden)
            }
        }
    }
    val dataState = MutableLiveData<DataState>()

//    init {
//        loadProducts()
//    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.menuItems().let {
                availableProducts.value = it
            }
//            availableProducts.value = listOf(
//                MenuProductItem("ariel", "Ariel", 15.0f, MeasureUnit.SACHET, 1f, ProductType.DETERGENT),
//                MenuProductItem("breeze", "Breeze", 15.0f, MeasureUnit.SACHET, 1f, ProductType.DETERGENT),
//                MenuProductItem("downy", "Downy", 15.0f, MeasureUnit.MILLILITER,  80f, ProductType.FAB_CON),
//                MenuProductItem("del", "Del", 15.0f, MeasureUnit.MILLILITER,  80f, ProductType.FAB_CON),
//                MenuProductItem("plastic-s", "Plastic (S)", 3.0f, MeasureUnit.PCS,  1f, ProductType.OTHER),
//                MenuProductItem("plastic-m", "Plastic (M)", 5.0f, MeasureUnit.PCS,  1f, ProductType.OTHER),
//                MenuProductItem("plastic-l", "Plastic (L)", 7.0f, MeasureUnit.PCS,  1f, ProductType.OTHER),
//                MenuProductItem("hanger", "Hanger", 25.0f, MeasureUnit.PCS, 1f, ProductType.OTHER),
//            )
        }
    }

    fun setPreselectedProducts(products: List<MenuProductItem>?) {
        viewModelScope.launch {
            _availableProducts.value = repository.menuItems().map {mpi ->
                products?.find { mpi.productRefId == it.productRefId }?.let {
                    mpi.joProductItemId = it.joProductItemId
                    mpi.quantity = it.quantity
                    mpi.selected = !it.deleted
                    mpi.deleted = mpi.deleted
                }
                mpi
            }
        }
//        products?.forEach { mpi ->
//            availableProducts.value?.find { mpi.productRefId.toString() == it.productRefId.toString() }?.apply {
//                this.joProductItemId = mpi.joProductItemId
//                this.quantity = mpi.quantity
//                this.selected = !mpi.deleted
//                this.deleted = mpi.deleted
//            }
//        }
    }

    fun putProduct(quantityModel: QuantityModel) {
        val product = _availableProducts.value?.find { it.productRefId == quantityModel.id }?.apply {
            selected = true
            quantity = quantityModel.quantity
            deleted = false
        }
        dataState.value = DataState.UpdateProduct(product!!)
    }

    fun removeProduct(quantityModel: QuantityModel) {
        _availableProducts.value?.find { it.productRefId == quantityModel.id }?.apply {
            if(this.joProductItemId != null) {
                // It's already in the database
                // Just mark deleted
                this.deleted = true
            }
            this.selected = false
            this.quantity = 0
            dataState.value = DataState.UpdateProduct(this)
        }
    }

    fun prepareSubmit() {
        _availableProducts.value?.let { list ->
            val validation = InputValidation()
            list.filter { it.selected } .onEach {
                if(it.joProductItemId == null && it.quantity > it.currentStock) {
                    validation.addError(it.productRefId.toString(), "Not enough stocks!")
                }
            }

            if(validation.isInvalid()) {
                dataState.value = DataState.Invalidate(validation)
                return@let
            }

            dataState.value = DataState.Submit(
                list.filter { it.selected || it.deleted }
            )
        }
    }

    fun resetState() {
        dataState.value = DataState.StateLess
    }

    sealed class DataState {
        object StateLess: DataState()
        data class Invalidate(val inputValidation: InputValidation) : DataState()
        data class UpdateProduct(val productItem: MenuProductItem) : DataState()
        data class Submit(val productItems: List<MenuProductItem>) : DataState()
    }
}