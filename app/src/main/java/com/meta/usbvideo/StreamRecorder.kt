import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.util.Date
import java.util.Locale

class StreamRecorder(private val application: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null
    private val TAG = "StreamRecorder"

    fun startRecording(width: Int = 1920, height: Int = 1080, frameRate: Int = 30) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = application.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            recordingFile = File(storageDir, "USB_VIDEO_$timestamp.mp4")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(application)
            } else {
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setVideoSize(width, height)
                setVideoFrameRate(frameRate)
                setOutputFile(recordingFile?.absolutePath)
                prepare()
                start()
            }
            Log.d(TAG, "Recording started at ${recordingFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            stopRecording()
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            Log.d(TAG, "Recording stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
            recordingFile = null
        }
    }

    fun getRecordingFile(): File? = recordingFile
}