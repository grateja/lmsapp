package com.vag.lmsapp.app.app_settings.shop_info

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopInfoViewModel

@Inject
constructor(
    private val shopRepository: ShopRepository
): CreateViewModel<EntityShop>(shopRepository) {
    val shop = shopRepository.getAsLiveData()

    fun get() {
        viewModelScope.launch {
            model.value = shopRepository.get() ?: EntityShop()
        }
    }

    fun validate() {
        val validation = InputValidation().apply {
            addRule("name", model.value?.name, arrayOf(Rule.Required))
            addRule("address", model.value?.address, arrayOf(Rule.Required))
        }

        super.isInvalid(validation)
    }
}