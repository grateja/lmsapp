package com.vag.lmsapp.settings

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeveloperSettingsRepository

@Inject
constructor(
    private val dao: SettingsDao
) : BaseSettingsRepository(dao) {
    companion object {
        const val DEVELOPER_ACTIVATION_DELAY = "activationDelay"
        const val DEVELOPER_FAKE_CONNECTION_MODE_ON = "face_connection_mode_on"
        const val MACHINE_IP_PREFIX = "networkPrefix"
        const val MACHINE_IP_SUBNET_MASK = "subnetMask"
        const val MACHINE_ACTIVATION_ENDPOINT = "endpoint"
        const val MACHINE_ACTIVATION_TIMEOUT = "activationConnectionTimeout"
    }
    val fakeConnectionMode = getAsLiveData(DEVELOPER_FAKE_CONNECTION_MODE_ON, false)
    val fakeConnectionDelay = getAsLiveData(DEVELOPER_ACTIVATION_DELAY, 3000L) //MutableLiveData(appPreferenceRepository.testFakeDelay())

    val prefix = getAsLiveData(MACHINE_IP_PREFIX, "192.168")
    val subnet = getAsLiveData(MACHINE_IP_SUBNET_MASK, "210")
    val endpoint = getAsLiveData(MACHINE_ACTIVATION_ENDPOINT, "activate")
    val timeout = getAsLiveData(MACHINE_ACTIVATION_TIMEOUT, 15L)

    suspend fun getFakeConnectionModeOn() = getValue(DEVELOPER_FAKE_CONNECTION_MODE_ON, false)
    suspend fun getFakeConnectionDelay() = getValue(DEVELOPER_ACTIVATION_DELAY, 3000L)

    suspend fun getPrefix() = getValue(MACHINE_IP_PREFIX, "192.168")
    suspend fun getSubnet() = getValue(MACHINE_IP_SUBNET_MASK, "210")
    suspend fun getEndpoint() = getValue(MACHINE_ACTIVATION_ENDPOINT, "activate")
    suspend fun getTimeout() = getValue(MACHINE_ACTIVATION_TIMEOUT, 15L)
}