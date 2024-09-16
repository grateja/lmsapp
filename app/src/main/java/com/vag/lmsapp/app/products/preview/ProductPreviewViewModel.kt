package com.vag.lmsapp.app.products.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductPreviewViewModel

@Inject
constructor(
    private val repository: ProductRepository
) : ViewModel(){
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _productId = MutableLiveData<UUID>()
    val productPreview = _productId.switchMap { repository.getProductPreviewAsLiveData(it) }

    fun setProductId(productId: UUID) {
        _productId.value = productId
    }

    fun editProduct() {
        _productId.value?.let {
            _navigationState.value = NavigationState.EditProduct(it)
        }
    }

    fun addStock() {
        _productId.value?.let {
            _navigationState.value = NavigationState.AddStock(it)
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            productPreview.value?.product?.let {
                repository.delete(it)
                _navigationState.value = NavigationState.DeleteSuccess(it.id)
            }
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun hideToggle() {
        viewModelScope.launch {
            _productId.value.let {
                repository.hideToggle(it)
            }
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class EditProduct(val productId: UUID): NavigationState()
        data class AddStock(val productId: UUID): NavigationState()
        data class DeleteSuccess(val productId: UUID): NavigationState()
    }
}