package com.vag.lmsapp.app.app_settings.text_message_templates

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import com.vag.lmsapp.room.repository.TextMessageTemplateRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextMessageTemplateListViewModel

@Inject
constructor(
    private val repository: TextMessageTemplateRepository
): ListViewModel<EntityTextMessageTemplate, BaseFilterParams>() {
    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }
        job = viewModelScope.launch {
            if(keyword.value != null) {
                delay(500)
            }

            if(reset) {
                page = 1
            }
            repository.filter(keyword.value ?: "", page).let {
                setResult(it, null, reset)
                loading.value = false
            }
        }

        job?.start()
    }


}