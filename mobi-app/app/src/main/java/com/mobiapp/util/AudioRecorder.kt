package com.mobiapp.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentFile: String = ""

    fun startRecording(outputPath: String): Boolean {
        return try {
            currentFile = outputPath
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION") MediaRecorder()
            }
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(outputPath)
                prepare()
                start()
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun stopRecording(): String? {
        return try {
            recorder?.apply { stop(); release() }
            recorder = null
            if (File(currentFile).exists()) currentFile else null
        } catch (e: Exception) {
            recorder = null
            null
        }
    }

    fun startPlayback(path: String, onComplete: () -> Unit) {
        stopPlayback()
        player = MediaPlayer().apply {
            setDataSource(path)
            setOnCompletionListener { onComplete() }
            prepare()
            start()
        }
    }

    fun stopPlayback() {
        player?.apply { if (isPlaying) stop(); release() }
        player = null
    }

    fun isPlaying() = player?.isPlaying == true

    fun release() {
        stopRecording()
        stopPlayback()
    }

    companion object {
        fun newRecordingPath(context: Context): String {
            val dir = File(context.filesDir, "voice_notes").apply { mkdirs() }
            return File(dir, "vn_${System.currentTimeMillis()}.m4a").absolutePath
        }
    }
}
