package com.vag.lmsapp.app.discounts.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumDiscountApplicable
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityDiscount
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiscountAddEditViewModel

@Inject
constructor(
    private val repository: DiscountsRepository
) : CreateViewModel<EntityDiscount>(repository) {
    fun get(id: UUID?) {
        viewModelScope.launch {
            val entity = super.get(id, EntityDiscount(listOf(
                EnumDiscountApplicable.TOTAL_AMOUNT
            )))

            _applicableTo.value = EnumDiscountApplicable.entries.map { enum ->
                DiscountApplicableViewModel(enum, entity.applicableTo.any {it.id == enum.id})
            }
        }
    }

    private val _applicableTo = MutableLiveData<List<DiscountApplicableViewModel>>()
    val applicableTo: LiveData<List<DiscountApplicableViewModel>> = _applicableTo

    fun syncApplicable(applicable: DiscountApplicableViewModel) {
        _applicableTo.value = _applicableTo.value?.apply {
            this.find {it.applicable.id == applicable.applicable.id}?.selected = applicable.selected
        }
    }

//    private val applicableTo = EnumDiscountApplicable.values().map {
//        DiscountApplicableViewModel(it, false)
//    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("name", model.value?.name, arrayOf(Rule.Required))
            addRule("value", model.value?.value, arrayOf(
                Rule.Required,
                Rule.IsNumeric,
                Rule.Min(1f, "Discount value cannot be 0"),
            ))

            addRule("value", model.value?.value, arrayOf(Rule.Max(100f, "Discount cannot be greater than 100%")))
        }
        super.isInvalid(inputValidation)
    }

    override fun save() {
//        this.validation.value = InputValidation().apply {
//            addRule("remarks", model.value?.name, arrayOf(Rule.Required))
//            addRule("amount", model.value?.value, arrayOf(Rule.Required, Rule.IsNumeric))
//            addRule("discountType", model.value?.discountType, arrayOf(Rule.Required))
//        }

        val applicable = this.applicableTo.value?.filter { it.selected }

        if(applicable == null || applicable.isEmpty()) {
            dataState.value = DataState.Invalidate("Please select at least one")
            return
        }

        model.value?.applicableTo = applicable.map { it.applicable }

        super.save()
    }
}