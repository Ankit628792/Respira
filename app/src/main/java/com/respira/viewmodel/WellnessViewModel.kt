package com.respira.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.respira.audio.CalmingAudioEngine
import com.respira.data.model.BreathingSessionRecord
import com.respira.data.model.Exercise
import com.respira.data.model.LungTestRecord
import com.respira.data.model.Milestone
import com.respira.data.model.UserSettings
import com.respira.data.repository.WellnessRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class BreathingPhase {
    PREPARING,
    INHALE,
    HOLD_IN,
    EXHALE,
    HOLD_OUT,
    FINISHED
}

enum class LungTestStage {
    READY,
    DEEP_INHALE,
    HOLDING,
    EXHALING,
    RESULT
}

data class SessionSummary(
    val exerciseTitle: String,
    val durationSeconds: Int,
    val streakDayCount: Int,
    val message: String,
    val category: String
)

class WellnessViewModel(application: Application) : AndroidViewModel(application) {
    val repository = WellnessRepository(application)
    val audioEngine = CalmingAudioEngine(application)

    val settings: StateFlow<UserSettings> = repository.settings

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<BreathingSessionRecord>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBreathingSeconds: StateFlow<Int> = repository.totalBreathingSeconds
        .combine(MutableStateFlow(0)) { total, _ -> total ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSessionsCount: StateFlow<Int> = repository.totalSessionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val latestLungTest: StateFlow<LungTestRecord?> = repository.latestLungTest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLungTests: StateFlow<List<LungTestRecord>> = repository.allLungTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category filter for Exercises tab
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Guided Exercise state
    private val _activeExercise = MutableStateFlow<Exercise?>(null)
    val activeExercise: StateFlow<Exercise?> = _activeExercise.asStateFlow()

    private val _currentPhase = MutableStateFlow(BreathingPhase.PREPARING)
    val currentPhase: StateFlow<BreathingPhase> = _currentPhase.asStateFlow()

    private val _phaseRemainingSeconds = MutableStateFlow(3)
    val phaseRemainingSeconds: StateFlow<Int> = _phaseRemainingSeconds.asStateFlow()

    private val _phaseDurationSeconds = MutableStateFlow(3)
    val phaseDurationSeconds: StateFlow<Int> = _phaseDurationSeconds.asStateFlow()

    private val _elapsedSessionSeconds = MutableStateFlow(0)
    val elapsedSessionSeconds: StateFlow<Int> = _elapsedSessionSeconds.asStateFlow()

    private val _targetSessionSeconds = MutableStateFlow(300)
    val targetSessionSeconds: StateFlow<Int> = _targetSessionSeconds.asStateFlow()

    private val _isSessionPaused = MutableStateFlow(false)
    val isSessionPaused: StateFlow<Boolean> = _isSessionPaused.asStateFlow()

    private val _isAmbientSoundEnabled = MutableStateFlow(true)
    val isAmbientSoundEnabled: StateFlow<Boolean> = _isAmbientSoundEnabled.asStateFlow()

    private val _completedSummary = MutableStateFlow<SessionSummary?>(null)
    val completedSummary: StateFlow<SessionSummary?> = _completedSummary.asStateFlow()

    // Lung Test state
    private val _lungTestStage = MutableStateFlow(LungTestStage.READY)
    val lungTestStage: StateFlow<LungTestStage> = _lungTestStage.asStateFlow()

    private val _lungTestHoldSeconds = MutableStateFlow(0)
    val lungTestHoldSeconds: StateFlow<Int> = _lungTestHoldSeconds.asStateFlow()

    private val _latestCreatedTestRecord = MutableStateFlow<LungTestRecord?>(null)
    val latestCreatedTestRecord: StateFlow<LungTestRecord?> = _latestCreatedTestRecord.asStateFlow()

    private var sessionJob: Job? = null
    private var testJob: Job? = null

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleExerciseDownload(exercise: Exercise) {
        viewModelScope.launch {
            repository.toggleExerciseDownload(exercise.id, exercise.isDownloaded)
        }
    }

    fun downloadAllExercises(downloaded: Boolean) {
        viewModelScope.launch {
            repository.setAllExercisesDownloaded(downloaded)
        }
    }

    // ==================== Guided Breathing Engine ====================
    fun startExercise(exercise: Exercise) {
        _activeExercise.value = exercise
        _isSessionPaused.value = false
        _elapsedSessionSeconds.value = 0
        _targetSessionSeconds.value = exercise.durationMinutes * 60
        _currentPhase.value = BreathingPhase.PREPARING
        _phaseRemainingSeconds.value = 3
        _phaseDurationSeconds.value = 3

        if (settings.value.soundEnabled && _isAmbientSoundEnabled.value) {
            audioEngine.startAmbient(exercise.ambientSound, settings.value.soundVolume)
        }

        startBreathingLoop(exercise)
    }

    private fun startBreathingLoop(exercise: Exercise) {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            // Initial 3-second preparation
            _currentPhase.value = BreathingPhase.PREPARING
            _phaseDurationSeconds.value = 3
            for (sec in 3 downTo 1) {
                _phaseRemainingSeconds.value = sec
                delay(1000)
            }

            while (isActive && _elapsedSessionSeconds.value < _targetSessionSeconds.value) {
                // 1. INHALE
                _currentPhase.value = BreathingPhase.INHALE
                _phaseDurationSeconds.value = exercise.inhaleSeconds
                if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.INHALE_START)
                runPhase(exercise.inhaleSeconds)
                if (!isActive) break

                // 2. HOLD AFTER INHALE (if any)
                if (exercise.holdAfterInhaleSeconds > 0) {
                    _currentPhase.value = BreathingPhase.HOLD_IN
                    _phaseDurationSeconds.value = exercise.holdAfterInhaleSeconds
                    if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.HOLD_START)
                    runPhase(exercise.holdAfterInhaleSeconds)
                    if (!isActive) break
                }

                // 3. EXHALE
                _currentPhase.value = BreathingPhase.EXHALE
                _phaseDurationSeconds.value = exercise.exhaleSeconds
                if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.EXHALE_START)
                runPhase(exercise.exhaleSeconds)
                if (!isActive) break

                // 4. HOLD AFTER EXHALE (if any)
                if (exercise.holdAfterExhaleSeconds > 0) {
                    _currentPhase.value = BreathingPhase.HOLD_OUT
                    _phaseDurationSeconds.value = exercise.holdAfterExhaleSeconds
                    if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.HOLD_START)
                    runPhase(exercise.holdAfterExhaleSeconds)
                    if (!isActive) break
                }
            }

            // Finished session
            finishExercise(completed = true)
        }
    }

    private suspend fun runPhase(seconds: Int) {
        for (sec in seconds downTo 1) {
            _phaseRemainingSeconds.value = sec
            while (_isSessionPaused.value && sessionJob?.isActive == true) {
                delay(200)
            }
            delay(1000)
            _elapsedSessionSeconds.value += 1
            if (_elapsedSessionSeconds.value >= _targetSessionSeconds.value) {
                break
            }
        }
    }

    fun togglePauseSession() {
        val isNowPaused = !_isSessionPaused.value
        _isSessionPaused.value = isNowPaused
        val currentEx = _activeExercise.value
        if (isNowPaused) {
            audioEngine.pauseAmbient()
        } else {
            if (currentEx != null && settings.value.soundEnabled && _isAmbientSoundEnabled.value) {
                audioEngine.resumeAmbient(currentEx.ambientSound, settings.value.soundVolume)
            }
        }
    }

    fun toggleAmbientSound() {
        val next = !_isAmbientSoundEnabled.value
        _isAmbientSoundEnabled.value = next
        val currentEx = _activeExercise.value
        if (next && currentEx != null && settings.value.soundEnabled && !_isSessionPaused.value) {
            audioEngine.startAmbient(currentEx.ambientSound, settings.value.soundVolume)
        } else {
            audioEngine.pauseAmbient()
        }
    }

    fun finishExercise(completed: Boolean) {
        sessionJob?.cancel()
        sessionJob = null
        audioEngine.stopAmbient()
        if (settings.value.hapticEnabled && completed) {
            audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.COMPLETED)
        }

        val ex = _activeExercise.value
        val elapsed = _elapsedSessionSeconds.value
        _currentPhase.value = BreathingPhase.FINISHED

        if (ex != null && elapsed >= 30) {
            viewModelScope.launch {
                val recordId = repository.recordSession(
                    exerciseId = ex.id,
                    title = ex.title,
                    category = ex.category,
                    durationSeconds = elapsed
                )
                val newStreak = settings.value.streakCount
                val encouragingMessages = listOf(
                    "Wonderful breathwork. Your nervous system is grounded and restored.",
                    "Great dedication. Conscious breathing revitalizes cellular oxygenation.",
                    "Session completed. Notice the stillness and clarity in your mind.",
                    "Excellent progress. Consistency expands your lung vital capacity."
                )
                _completedSummary.value = SessionSummary(
                    exerciseTitle = ex.title,
                    durationSeconds = elapsed,
                    streakDayCount = newStreak,
                    message = encouragingMessages.random(),
                    category = ex.category
                )
            }
        } else {
            _activeExercise.value = null
        }
    }

    fun dismissSummary() {
        _completedSummary.value = null
        _activeExercise.value = null
    }

    fun exitSessionEarly() {
        sessionJob?.cancel()
        sessionJob = null
        audioEngine.stopAmbient()
        _activeExercise.value = null
        _completedSummary.value = null
    }

    // ==================== Lung Capacity Test ====================
    fun startLungCapacityTest() {
        testJob?.cancel()
        _lungTestStage.value = LungTestStage.READY
        _lungTestHoldSeconds.value = 0
        _latestCreatedTestRecord.value = null
    }

    fun proceedToDeepInhale() {
        _lungTestStage.value = LungTestStage.DEEP_INHALE
        if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.INHALE_START)
    }

    fun startHoldingBreath() {
        _lungTestStage.value = LungTestStage.HOLDING
        _lungTestHoldSeconds.value = 0
        if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.HOLD_START)

        testJob?.cancel()
        testJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _lungTestHoldSeconds.value += 1
            }
        }
    }

    fun completeLungTestHold() {
        testJob?.cancel()
        testJob = null
        _lungTestStage.value = LungTestStage.RESULT
        if (settings.value.hapticEnabled) audioEngine.triggerHaptic(CalmingAudioEngine.HapticType.EXHALE_START)

        val holdSeconds = _lungTestHoldSeconds.value.coerceAtLeast(5)
        // Clinically inspired vital capacity estimation formula:
        // baseline volume ~ 3.2L + (holdSeconds * 0.026L) clamped between 3.0L and 6.0L
        val estimatedLiters = (3.2f + (holdSeconds * 0.026f)).coerceIn(3.0f, 6.0f)
        val roundedLiters = Math.round(estimatedLiters * 10f) / 10f

        viewModelScope.launch {
            val record = repository.recordLungTest(holdSeconds, roundedLiters)
            _latestCreatedTestRecord.value = record
        }
    }

    fun dismissLungTest() {
        testJob?.cancel()
        testJob = null
        _lungTestStage.value = LungTestStage.READY
    }

    // ==================== Settings & User Controls ====================
    fun updateSettings(newSettings: UserSettings) {
        repository.updateSettings(newSettings)
        audioEngine.setVolume(newSettings.soundVolume)
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    fun getMilestones(): List<Milestone> {
        val totalSessions = totalSessionsCount.value
        val totalMins = totalBreathingSeconds.value / 60
        val streak = settings.value.streakCount
        return repository.getMilestones(totalSessions, totalMins, streak)
    }

    override fun onCleared() {
        super.onCleared()
        sessionJob?.cancel()
        testJob?.cancel()
        audioEngine.stopAmbient()
    }
}
