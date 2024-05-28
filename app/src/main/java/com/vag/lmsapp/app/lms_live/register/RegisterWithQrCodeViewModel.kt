package com.vag.lmsapp.app.lms_live.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.dao.BranchDao
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.MoshiHelper
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterWithQrCodeViewModel

@Inject
constructor(
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository,
    private val moshiHelper: MoshiHelper,
    private val sanctumRepository: SanctumRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    val shop = shopRepository.getAsLiveData()
    fun link(result: String) {
        viewModelScope.launch {
            try {
                val result = moshiHelper.decodeShopLinkQrCode(result)
                println("qr code result")
                println(result)
                val shop = shop.value ?: return@launch
                println("shop")
                println(shop)
                val ownerId = result?.userId ?: return@launch
                println("sending request")
                networkRepository.link(shop, ownerId, result.token).let {
                    println("result from retrofit")
                    println(it)
                    it.getOrNull()?.let {
                        sanctumRepository.save(it)
                    }
                }
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