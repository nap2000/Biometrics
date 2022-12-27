package uk.ac.lshtm.keppel.android.scanning

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.ac.lshtm.keppel.android.scanning.ScannerState.*
import uk.ac.lshtm.keppel.android.core.Scanner
import uk.ac.lshtm.keppel.android.core.TaskRunner

class ScannerViewModel(
    private val scanner: Scanner,
    private val taskRunner: TaskRunner
) : ViewModel() {

    private val _scannerState = MutableLiveData(DISCONNECTED)
    private val _fingerTemplate = MutableLiveData<String>(null)
    private val _fingerWSQ = MutableLiveData<ByteArray>(null)

    val scannerState: LiveData<ScannerState> = _scannerState
    val fingerTemplate: LiveData<String> = _fingerTemplate
    val fingerWSQ: LiveData<ByteArray> = _fingerWSQ
    var type: String = "iso";

    init {
        scanner.connect {
            _scannerState.value = CONNECTED
        }

        scanner.onDisconnect {
            _scannerState.value = DISCONNECTED
        }
    }

    fun capture(context: Context) {
        _scannerState.value = SCANNING

        taskRunner.execute {

            if(type == "wsq") {
                val wsqImage = scanner.captureWSQImage(context)
                _fingerWSQ.postValue(wsqImage)
            } else {
                val isoTemplate = scanner.captureISOTemplate()
                _fingerTemplate.postValue(isoTemplate)
            }
            _scannerState.postValue(CONNECTED)

        }
    }

    fun stopCapture() {
        scanner.stopCapture()
    }

    public override fun onCleared() {
        scanner.stopCapture()
        scanner.disconnect()
    }
}

enum class ScannerState {
    DISCONNECTED, CONNECTED, SCANNING
}

class ScannerViewModelFactory(private val scanner: Scanner, private val taskRunner: TaskRunner) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScannerViewModel(scanner, taskRunner) as T
    }
}