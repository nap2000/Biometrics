package uk.ac.lshtm.keppel.android.scanning.scanners

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import uk.ac.lshtm.keppel.android.scanning.ScannerFactory
import uk.ac.lshtm.keppel.android.core.Scanner
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DemoScannerFactory : ScannerFactory {

    override val name: String = "Demo Scanner"

    override fun create(context: Context): Scanner =
        DemoScanner()
}

private class DemoScanner : Scanner {

    override fun connect(onConnected: () -> Unit): Scanner {
        Handler().postDelayed(onConnected, 3000)
        return this
    }

    override fun onDisconnect(onDisconnected: () -> Unit) {

    }

    override fun captureISOTemplate(): String {
        Thread.sleep(3000)
        return "demo-finger-print-iso-template"
    }

    override fun captureImage(context: Context): ByteArray? {

        Thread.sleep(3000)
        val fis: InputStream = context.getAssets().open("sample.png")

        val bm = BitmapFactory.decodeStream(fis)
        val stream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 70, stream)
        return stream.toByteArray()
        return "demo-finger-print-image".toByteArray()
    }

    override fun stopCapture() {

    }

    override fun disconnect() {

    }
}
