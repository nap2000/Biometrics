package uk.ac.lshtm.keppel.android.scanning

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import kotlinx.android.synthetic.main.activity_scan.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.LooperMode
import uk.ac.lshtm.keppel.android.Keppel
import uk.ac.lshtm.keppel.android.core.Scanner

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ScanActivityTest {

    private val fakeScanner = spy(FakeScanner())
    private lateinit var activity: ScanActivity
    private lateinit var activityController: ActivityController<ScanActivity>

    @Before
    fun setup() {
        ApplicationProvider.getApplicationContext<Keppel>().availableScanners =
            listOf(FakeScannerFactory(fakeScanner))
        ApplicationProvider.getApplicationContext<Keppel>().configureDefaultScanner(override = true)
        activityController = Robolectric.buildActivity(ScanActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun whenScannerDisconnected_showsConnectProgressBar() {
        assertThat(activity.capture_button.visibility, equalTo(View.GONE))
        assertThat(activity.capture_progress_bar.visibility, equalTo(View.GONE))
        assertThat(activity.connect_progress_bar.visibility, equalTo(View.VISIBLE))
    }

    @Test
    fun whenScannerConnected_clickingCapture_showsProgressBar() {
        fakeScanner.connect()

        activity.capture_button.performClick()
        assertThat(activity.capture_button.visibility, equalTo(View.GONE))
        assertThat(activity.capture_progress_bar.visibility, equalTo(View.VISIBLE))
        assertThat(activity.connect_progress_bar.visibility, equalTo(View.GONE))
    }

    @Test
    fun whenScannerConnected_thenDisconnected_showsConnectProgressBar() {
        fakeScanner.connect()
        fakeScanner.disconnect()

        assertThat(activity.capture_button.visibility, equalTo(View.GONE))
        assertThat(activity.capture_progress_bar.visibility, equalTo(View.GONE))
        assertThat(activity.connect_progress_bar.visibility, equalTo(View.VISIBLE))
    }

    @Test
    fun pausing_stopsCapture() {
        activityController.pause()
        verify(fakeScanner).stopCapture()
    }
}

class FakeScannerFactory(private val fakeScanner: FakeScanner) : ScannerFactory {

    override val name: String = "Fake"

    override fun create(context: Context): Scanner = fakeScanner
}

open class FakeScanner : Scanner {

    private var onDisconnected: (() -> Unit)? = null
    private lateinit var onConnected: () -> Unit

    fun connect() {
        onConnected()
    }

    override fun connect(onConnected: () -> Unit): Scanner {
        this.onConnected = onConnected
        return this
    }

    override fun onDisconnect(onDisconnected: () -> Unit) {
        this.onDisconnected = onDisconnected
    }

    override fun captureISOTemplate(): String {
        return ""
    }

    override fun captureImage(context: Context): ByteArray {
        return "".toByteArray()
    }

    override fun stopCapture() {

    }

    override fun disconnect() {
        onDisconnected?.invoke()
    }
}