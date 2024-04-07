package com.vag.lmsapp.app.products.add_stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityInventoryLog
import com.vag.lmsapp.room.repository.InventoryLogRepository
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductAddStockViewModel

@Inject
constructor(
    private val repository: ProductRepository,
    private val inventoryLogRepository: InventoryLogRepository
) : CreateViewModel<EntityInventoryLog>(inventoryLogRepository){
    private val _productId = MutableLiveData<UUID?>()
    val product = _productId.switchMap { repository.getProductAsLiveData(it) }

    val addAsExpense = MutableLiveData(false)

    fun get(inventoryLogId: UUID?, productId: UUID?) {
        viewModelScope.launch {
            super.get(inventoryLogId, EntityInventoryLog(
                productId = productId
            )).let {
                _productId.value = it.productId
            }
        }
    }

    fun validate() {
        val validation = InputValidation().apply {
            addRule("quantity", model.value?.quantity, arrayOf(Rule.Required, Rule.IsNumeric, Rule.Min(1f)))

            if(addAsExpense.value == true) {
                addRule("totalAmount", model.value?.totalAmount, arrayOf(Rule.Required, Rule.IsNumeric, Rule.Min(1f)))
            }
        }
        super.isInvalid(validation)
    }

    fun save(authId: UUID) {
        viewModelScope.launch {
            val model = model.value?.apply {
                userId = authId
            } ?: return@launch

            val expense = if(addAsExpense.value == true) {
                EntityExpense(
                    remarks = "Purchase of ${product.value?.name}",
                    amount = model.totalAmount,
                    tag = "product_purchase",
                    createdBy = authId
                )
            } else {
                null
            }

            inventoryLogRepository.save(model, expense).let {
                dataState.value = DataState.SaveSuccess(model)
            }
        }
    }
}