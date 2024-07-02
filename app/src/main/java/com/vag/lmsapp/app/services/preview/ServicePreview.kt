package com.vag.lmsapp.app.services.preview

import androidx.room.Embedded
import com.vag.lmsapp.room.entities.EntityService

data class ServicePreview(
    @Embedded
    val service: EntityService
)
