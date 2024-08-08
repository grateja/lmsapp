package com.vag.lmsapp.app.machines.ping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.MachineConnectionStatus
import com.vag.lmsapp.settings.DeveloperSettingsRepository
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PingViewModel

@Inject
constructor(
    private val settingsRepository: DeveloperSettingsRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<String>>()
    val dataState: LiveData<DataState<String>> = _dataState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var thread: Thread? = null

    fun cancel() {
        thread = null
    }

    fun start(ipEnd: Int) {
        thread = null

        _loading.value = true

        thread = Thread {
            runBlocking {
                try {
                    val prefix = settingsRepository.getPrefix()
                    val subnet = settingsRepository.getSubnet()
                    val ipAddress = "$prefix.$subnet.${ipEnd}"
                    val url = "http://$ipAddress/details"

                    val request = Request.Builder()
                        .url(url)
                        .build()

                    client.newCall(request).apply {
                        execute().let { response ->
                            viewModelScope.launch {
                                if(response.code() == 200) {
                                    _dataState.value = DataState.Invalidate("Connection success!")
                                } else {
                                    _dataState.value = DataState.Invalidate("Connection failed!")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    viewModelScope.launch {
                        _dataState.value = DataState.Invalidate(e.message.toString())
                    }
                    e.printStackTrace()
                } finally {
                    viewModelScope.launch {
                        _loading.value = false
                    }
                }
            }
        }

        thread?.start()
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .cache(null)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }
}