package com.vag.lmsapp.app.joborders.create.discount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.csi.palabakosys.model.DiscountTypeEnum
import com.vag.lmsapp.room.repository.DiscountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscountViewModel

@Inject
constructor(
    private val repository: DiscountsRepository
) : ViewModel()
{
    val discounts = MutableLiveData<List<MenuDiscount>>()
    val discount = MutableLiveData<MenuDiscount?>()

    fun setDiscount(discount: MenuDiscount?) {
        discounts.value?.onEach {
            if(it.selected) {
                // deselect all the discounts first
                it.selected = false
            }
        }
        this.discount.value = discounts.value?.find { it.discountRefId == discount?.discountRefId }?.apply {
            selected = true
        }
    }

    fun loadDiscounts() {
        viewModelScope.launch {
            discounts.value = repository.getAll("")
//            discounts.value = listOf(
//                MenuDiscount("birthday", "Birthday", 5f, DiscountTypeEnum.PERCENTAGE, listOf(DiscountApplicable.WASH_DRY_SERVICES)),
//                MenuDiscount("pwd", "PWD", 5f, DiscountTypeEnum.PERCENTAGE, listOf(DiscountApplicable.DELIVERY)),
//                MenuDiscount("senior", "Senior", 5f, DiscountTypeEnum.PERCENTAGE, listOf(DiscountApplicable.TOTAL_AMOUNT)),
//                MenuDiscount("opening", "Opening", 10f, DiscountTypeEnum.FIXED, listOf(DiscountApplicable.WASH_DRY_SERVICES, DiscountApplicable.PRODUCTS_CHEMICALS)),
//            )
        }
    }
}