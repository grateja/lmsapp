package com.vag.lmsapp.app.extras

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityExtras
import com.vag.lmsapp.room.entities.EntityUser

data class ExtrasItemFull (
    @Embedded
    val extras: EntityExtras
)