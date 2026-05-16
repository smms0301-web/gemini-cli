package com.mobiapp.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentFile: File? = null

    fun startRecording(): File {
        val dir = File(context.getExternalFilesDir(null), "Audio").also { it.mkdirs() }
        val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
        currentFile = file

        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return file
    }

    fun stopRecording(): File? {
        recorder?.apply { stop(); release() }
        recorder = null
        return currentFile
    }

    fun startPlayback(filePath: String) {
        player?.release()
        player = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    fun stopPlayback() {
        player?.apply { if (isPlaying) stop(); release() }
        player = null
    }

    fun release() {
        recorder?.release()
        player?.release()
        recorder = null
        player = null
    }
}
