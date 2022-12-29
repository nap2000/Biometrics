package uk.ac.lshtm.keppel.android.scanning

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_scan.*
import uk.ac.lshtm.keppel.android.R
import uk.ac.lshtm.keppel.android.scannerFactory
import uk.ac.lshtm.keppel.android.taskRunner
import java.io.File
import java.io.FileOutputStream


class ScanActivity : AppCompatActivity() {

    private lateinit var viewModel: ScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        viewModel = ViewModelProvider(
            this, ScannerViewModelFactory(
                scannerFactory().create(this),
                taskRunner()
            )
        ).get(ScannerViewModel::class.java)

        // Get parameters
        viewModel.type = intent.getStringExtra("type").toString()
        if(viewModel.type == null) {
            viewModel.type = "iso"
        } else {
            viewModel.type = viewModel.type.toLowerCase();
        }

        viewModel.scannerState.observe(this, Observer { state ->
            when (state) {
                ScannerState.DISCONNECTED -> {
                    connect_progress_bar.visibility = View.VISIBLE
                    capture_button.visibility = View.GONE
                    capture_progress_bar.visibility = View.GONE
                }

                ScannerState.CONNECTED -> {
                    connect_progress_bar.visibility = View.GONE
                    capture_button.visibility = View.VISIBLE
                    capture_progress_bar.visibility = View.GONE
                }

                ScannerState.SCANNING -> {
                    connect_progress_bar.visibility = View.GONE
                    capture_button.visibility = View.GONE
                    capture_progress_bar.visibility = View.VISIBLE
                }

                else -> {
                    // Ignore null case - not expected
                }
            }
        })

        viewModel.fingerTemplate.observe(this, Observer { template ->
            if (template != null) {
                intent.putExtra("value", template)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        viewModel.fingerImage.observe(this, Observer { image ->
            if (image != null) {

                val imagePath = File(applicationContext.filesDir, "scan_images")
                if(!imagePath.exists()) {
                    imagePath.mkdir();
                }
                val outputFile = File.createTempFile("fpr", ".png", imagePath)
                val of = FileOutputStream(outputFile);
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, of);

                val uri = getUriForFile(applicationContext, "au.com.smap.fileprovider", outputFile)

                val returnIntent = Intent();
                returnIntent.clipData = ClipData.newRawUri("fpr.png", uri);
                returnIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                setResult(RESULT_OK, returnIntent);

                finish()
            }
        })

        capture_button.setOnClickListener {
            viewModel.capture(applicationContext)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopCapture()
    }
}