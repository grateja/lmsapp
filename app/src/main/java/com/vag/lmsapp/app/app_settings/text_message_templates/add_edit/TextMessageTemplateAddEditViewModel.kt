package com.vag.lmsapp.app.app_settings.text_message_templates.add_edit

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import com.vag.lmsapp.room.repository.TextMessageTemplateRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TextMessageTemplateAddEditViewModel

@Inject
constructor(
    private val repository: TextMessageTemplateRepository
): CreateViewModel<EntityTextMessageTemplate>(repository) {
    fun get(id: UUID?) {
        viewModelScope.launch {
            super.get(id, EntityTextMessageTemplate("", ""))
        }
    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("title", model.value?.title, arrayOf(Rule.Required))
            addRule("message", model.value?.message, arrayOf(Rule.Required))
        }
        super.isInvalid(inputValidation)
    }
}