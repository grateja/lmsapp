package com.vag.lmsapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sanctum_tokens")
data class SanctumToken(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val plainTextToken: String,
    val abilities: String?
)