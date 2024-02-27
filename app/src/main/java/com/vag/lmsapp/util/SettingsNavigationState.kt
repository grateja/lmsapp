package com.vag.lmsapp.util

sealed class SettingsNavigationState {
    object StateLess: SettingsNavigationState()
    class OpenSettings(val value: Any, val key: String, val title: String?, val message: String?, val type: Class<*>?): SettingsNavigationState()
    class OpenIntSettings(val value: Int, val key: String, val title: String?, val message: String?): SettingsNavigationState()
    class OpenStringSettings(val value: String, val key: String, val title: String?, val message: String?): SettingsNavigationState()
    class OpenFloatSettings(val value: Float, val key: String, val title: String?, val message: String?): SettingsNavigationState()
    class OpenLongSettings(val value: Long, val key: String, val title: String?, val message: String?): SettingsNavigationState()
    class OpenBooleanSettings(val value: Boolean, val key: String, val title: String?, val message: String?): SettingsNavigationState()
}
