package com.respira.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Procedural offline synthesizer producing gentle, relaxing ambient soundscapes
 * (such as calming ocean wave oscillations and warm soothing resonant chords)
 * completely offline without external internet streaming or large media assets.
 */
class CalmingAudioEngine(private val context: Context) {
    private var audioTrack: AudioTrack? = null
    private var soundJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var isPlaying = false

    @Volatile
    private var volume = 0.5f

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun startAmbient(soundType: String, soundVolume: Float) {
        stopAmbient()
        this.volume = soundVolume.coerceIn(0f, 1f)
        isPlaying = true

        val sampleRate = 22050
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            soundJob = scope.launch {
                val buffer = ShortArray(1024)
                var phase1 = 0.0
                var phase2 = 0.0
                var swellPhase = 0.0

                // Base gentle meditation frequencies (432Hz ambient tuning or 108Hz drone)
                val baseFreq = when (soundType) {
                    "Ocean Waves" -> 136.1 // Om / Earth frequency
                    "Zen Stream" -> 174.0 // Solfeggio soothing
                    "Deep Drone" -> 108.0
                    "Forest Rain" -> 216.0
                    else -> 136.1
                }

                while (isActive && isPlaying) {
                    val twoPi = 2.0 * Math.PI
                    val delta1 = twoPi * baseFreq / sampleRate
                    val delta2 = twoPi * (baseFreq * 1.5) / sampleRate
                    val swellDelta = twoPi * 0.12 / sampleRate // Slow 8-second swell cycle

                    for (i in buffer.indices) {
                        phase1 += delta1
                        if (phase1 > twoPi) phase1 -= twoPi

                        phase2 += delta2
                        if (phase2 > twoPi) phase2 -= twoPi

                        swellPhase += swellDelta
                        if (swellPhase > twoPi) swellPhase -= twoPi

                        // Gentle wave modulation (ebb & flow between 0.3 and 1.0)
                        val swell = (0.65 + 0.35 * sin(swellPhase)).toFloat()
                        val sample1 = sin(phase1)
                        val sample2 = sin(phase2) * 0.35

                        val mixed = (sample1 + sample2) * swell * volume * 0.22
                        val clamped = (mixed.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                        buffer[i] = clamped
                    }

                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
        } catch (_: Exception) {
            // AudioTrack fallback gracefully
        }
    }

    fun setVolume(newVolume: Float) {
        this.volume = newVolume.coerceIn(0f, 1f)
    }

    fun stopAmbient() {
        isPlaying = false
        soundJob?.cancel()
        soundJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
    }

    fun triggerHaptic(type: HapticType) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (type) {
                        HapticType.INHALE_START -> VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.HOLD_START -> VibrationEffect.createOneShot(30, 80)
                        HapticType.EXHALE_START -> VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 30), intArrayOf(0, 70, 0, 90), -1)
                        HapticType.COMPLETED -> VibrationEffect.createWaveform(longArrayOf(0, 80, 100, 120), intArrayOf(0, 120, 0, 180), -1)
                    }
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(50)
                }
            }
        } catch (_: Exception) {
        }
    }

    enum class HapticType {
        INHALE_START,
        HOLD_START,
        EXHALE_START,
        COMPLETED
    }
}
