package com.vag.lmsapp.app.lms_live.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.dao.BranchDao
import com.vag.lmsapp.room.entities.SanctumToken
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.DataState
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
    private val sanctumRepository: SanctumRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<SanctumToken>>()
    val dataState: LiveData<DataState<SanctumToken>> = _dataState

    fun link(qrCode: ShopLinkQrCode) {
        viewModelScope.launch {
            try {
                val shop = shopRepository.get()

                if(shop == null) {
                    _dataState.value = DataState.Invalidate("Shop is not setup yet")
                    return@launch
                }

                networkRepository.link(shop, qrCode.userId, qrCode.token).let {
                    println("result from retrofit")
                    if(it.isSuccess) {
                        it.getOrNull()?.let {
                            sanctumRepository.save(it)
                            _dataState.value = DataState.SaveSuccess(it)
                        }
                    } else {
                        _dataState.value = DataState.Invalidate("QR Code expired!")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearState() {
        _dataState.value = DataState.StateLess
    }
}