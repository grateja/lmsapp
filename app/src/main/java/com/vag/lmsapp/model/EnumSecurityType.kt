package com.vag.lmsapp.model

enum class EnumSecurityType(
    val label: String,
    val description: String
) {
    START_UP(
        label = "Start up",
        description = "Require authentication every time the app is opened"
    ),
    SENSITIVE_ACTIONS(
        label = "Sensitive Actions (Recommended)",
        description = "Require authentication for every sensitive action"
    ),
    NONE(
        label = "None",
        description = "No authentication required (Not recommended for multiple staff)"
    )
}