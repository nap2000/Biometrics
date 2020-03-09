package uk.ac.lshtm.mantra.android.scanning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.ac.lshtm.mantra.android.scanning.ScannerState.*
import uk.ac.lshtm.mantra.core.Scanner
import uk.ac.lshtm.mantra.core.TaskRunner

class ScannerViewModel(
    private val scanner: Scanner,
    private val taskRunner: TaskRunner
) : ViewModel() {

    private val _scannerState = MutableLiveData(DISCONNECTED)
    private val _fingerTemplate = MutableLiveData<String>(null)

    val scannerState: LiveData<ScannerState> = _scannerState
    val fingerTemplate: LiveData<String> = _fingerTemplate

    init {
        scanner.connect {
            _scannerState.value = CONNECTED
        }
    }

    fun capture() {
        _scannerState.value = SCANNING

        taskRunner.execute {
            val isoTemplate = scanner.captureISOTemplate()
            _scannerState.postValue(CONNECTED)
            _fingerTemplate.postValue(isoTemplate)
        }
    }

    public override fun onCleared() {
        scanner.disconnect()
    }
}

enum class ScannerState {
    DISCONNECTED, CONNECTED, SCANNING
}

class ScannerViewModelFactory(private val scanner: Scanner, private val taskRunner: TaskRunner) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScannerViewModel(scanner, taskRunner) as T
    }
}