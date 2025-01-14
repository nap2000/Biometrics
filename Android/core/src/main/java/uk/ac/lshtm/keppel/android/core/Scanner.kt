package uk.ac.lshtm.keppel.android.core

import android.content.Context

interface Scanner {

    fun connect(onConnected: () -> Unit): Scanner

    fun onDisconnect(onDisconnected: () -> Unit)

    /**
     * Captures and returns a hex encoded string of the ISO 19794-2 Template from a finger
     * placed on the scanner
     */
    fun captureISOTemplate(): String?

    fun captureImage(context: Context): ByteArray?

    fun stopCapture()

    fun disconnect()
}