package com.vag.lmsapp.app.expenses.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.repository.ExpensesRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseAddEditViewModel

@Inject
constructor(
    private val expensesRepository: ExpensesRepository
) : CreateViewModel<EntityExpense>(expensesRepository) {

    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    val tags = expensesRepository.getTags()

    fun get(id: UUID?) {
        viewModelScope.launch {
            super.get(id, EntityExpense())
        }
    }

//    fun getDateCreated() : Instant {
//        return model.value?.createdAt ?: Instant.now()
//    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("remarks", model.value?.remarks, arrayOf(Rule.Required))
            addRule("amount", model.value?.amount, arrayOf(Rule.Required, Rule.IsNumeric))
            addRule("createdAt", model.value?.createdAt,
                arrayOf(Rule.Required,
                    Rule.NotAfter(Instant.now())
                )
            )
        }

        super.isInvalid(inputValidation)
    }

    fun save(userId: UUID?) {

//        val model = model.value
//
//        model?.createdBy = userId
//
//        this.validation.value = InputValidation().apply {
//            addRule("remarks", model?.remarks, arrayOf(Rule.Required))
//            addRule("amount", model?.amount, arrayOf(Rule.Required, Rule.IsNumeric))
////            addRules("tag", model.value?.tag, arrayOf(Rule.Required))
//        }
        model.value?.createdBy = userId
        model.value?.sync = false
        super.save()
    }

    fun showDateTimePicker() {
        val createdAt = model.value?.createdAt ?: Instant.now()
        _navigationState.value = NavigationState.ShowDateTimePicker(createdAt)
    }

    fun setDateCreated(dateTime: Instant) {
        model.value = model.value?.apply {
            createdAt = dateTime
        }
    }

    fun resetNavigationState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class ShowDateTimePicker(val dateTime: Instant): NavigationState()
    }
}