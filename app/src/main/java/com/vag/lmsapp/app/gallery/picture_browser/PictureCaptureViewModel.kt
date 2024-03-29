package com.vag.lmsapp.app.gallery.picture_browser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vag.lmsapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PictureCaptureViewModel

@Inject
constructor(
    application: Application
) : AndroidViewModel(application) {
    private val _navigationState = MutableLiveData<NavigationState>()
    val dataState: LiveData<NavigationState> = _navigationState

    private val _uri = MutableLiveData<String?>()
    val uri: LiveData<String?> = _uri

    private val _capturing = MutableLiveData(false)
    val capturing: LiveData<Boolean> = _capturing

    fun setCapturing(capturing: Boolean) {
        _capturing.value = capturing
    }

    fun setUri(uri: String) {
        _uri.value = uri
    }

    fun submit() {
        _uri.value?.let {
            _navigationState.value = NavigationState.Capture(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun discard() {
        _uri.value?.let { uri ->
            try {
                val filesDir = getApplication<Application>().filesDir
                File(filesDir, Constants.PICTURES_DIR + uri).let { _file ->
                    if(_file.exists()) {
                        _file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uri.value = null
                _navigationState.value = NavigationState.Discard
            }
        }
    }

    fun requestExit() {
        _navigationState.value = NavigationState.RequestExit(_uri.value == null)
    }

    sealed class NavigationState {
        data class Capture(val uri: String): NavigationState()
        object Discard: NavigationState()
        object StateLess: NavigationState()
        data class RequestExit(val canExit: Boolean): NavigationState()
    }
}