package com.connor.hindsight

import android.app.Activity
import androidx.activity.ComponentActivity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.connor.hindsight.ui.screens.MainScreen
import com.connor.hindsight.models.RecorderModel

class MainActivity : ComponentActivity() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>
    private val recorderModel: RecorderModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                recorderModel.startVideoRecorder(this, result)
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun requestScreenCapturePermission() {
        if (recorderModel.hasScreenRecordingPermissions(this)) {
            Log.d("MainActivity", "hasScreenRecordingPermissions")
            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
            screenCaptureLauncher.launch(captureIntent)
        }
    }

    fun stopScreenRecording() {
        recorderModel.stopRecording()
    }
}

@Preview(showBackground = true)
@Composable
fun HindsightPreview() {
    MainScreen()
}