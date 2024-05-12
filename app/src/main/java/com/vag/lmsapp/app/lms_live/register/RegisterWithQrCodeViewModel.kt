package com.vag.lmsapp.app.lms_live.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.dao.BranchDao
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterWithQrCodeViewModel

@Inject
constructor(
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    val shop = shopRepository.getAsLiveData()
    fun link(result: String) {
        viewModelScope.launch {
            try {
                val shop = shop.value ?: return@launch
                val ownerId = result.toUUID() ?: return@launch
                networkRepository.link(shop, ownerId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
    }
}