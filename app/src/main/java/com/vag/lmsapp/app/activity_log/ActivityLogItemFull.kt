package com.vag.lmsapp.app.activity_log

import androidx.room.Embedded
import com.vag.lmsapp.room.entities.EntityActivityLog

data class ActivityLogItemFull(
    @Embedded
    val activityLog: EntityActivityLog
)
