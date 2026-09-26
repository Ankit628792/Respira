package com.respira.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.respira.data.db.AppDatabase
import com.respira.data.model.BreathingSessionRecord
import com.respira.data.model.Exercise
import com.respira.data.model.LungTestRecord
import com.respira.data.model.Milestone
import com.respira.data.model.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WellnessRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val exerciseDao = db.exerciseDao()
    private val sessionDao = db.sessionDao()
    private val lungTestDao = db.lungTestDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("respira_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
    val allSessions: Flow<List<BreathingSessionRecord>> = sessionDao.getAllSessions()
    val recentSessions: Flow<List<BreathingSessionRecord>> = sessionDao.getRecentSessions()
    val totalBreathingSeconds: Flow<Int?> = sessionDao.getTotalBreathingSeconds()
    val totalSessionsCount: Flow<Int> = sessionDao.getTotalSessionsCount()
    val latestLungTest: Flow<LungTestRecord?> = lungTestDao.getLatestTest()
    val allLungTests: Flow<List<LungTestRecord>> = lungTestDao.getAllTests()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultDataIfNeeded()
        }
    }

    private suspend fun seedDefaultDataIfNeeded() {
        val currentSeedVersion = prefs.getInt("exercises_seed_version_v2", 0)
        val targetSeedVersion = 2
        
        if (exerciseDao.getCount() == 0 || currentSeedVersion < targetSeedVersion) {
            val masterExercises = getMasterExerciseList()
            exerciseDao.insertExercises(masterExercises)
            prefs.edit().putInt("exercises_seed_version_v2", targetSeedVersion).apply()
        }

        // Purge any pre-existing dummy data on first launch after reset update
        val hasPurged = prefs.getBoolean("has_purged_dummy_data_v3", false)
        if (!hasPurged) {
            sessionDao.clearAllSessions()
            lungTestDao.clearAllTests()
            prefs.edit().putInt("streak_count", 0).putBoolean("has_purged_dummy_data_v3", true).apply()
            _settings.value = loadSettings()
        }
    }

    private fun getMasterExerciseList(): List<Exercise> {
        return listOf(
            Exercise(
                id = "box_breathing",
                title = "Box Breathing (4-4-4-4)",
                category = "Relaxation",
                description = "A powerful technique used by athletes to steady the nervous system, lower cortisol, and heighten mental clarity.",
                durationMinutes = 5,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 4,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 4,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Ocean Waves",
                benefits = "Normalizes autonomic heart rate variability",
                downloadSizeMb = 12.4f
            ),
            Exercise(
                id = "diaphragmatic_lung",
                title = "Diaphragmatic Lung Expansion",
                category = "Lung Capacity",
                description = "Engages lower lung lobes to maximize vital air capacity, strengthen diaphragm muscles, and improve oxygenation.",
                durationMinutes = 6,
                difficulty = "Intermediate",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 5,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Strengthens diaphragm & expands lower lung lobes",
                downloadSizeMb = 15.0f
            ),
            Exercise(
                id = "4_7_8_relax",
                title = "4-7-8 Calming Reset",
                category = "Relaxation",
                description = "Dr. Andrew Weil's renowned natural tranquilizer for the nervous system that rapidly shifts the body into rest-and-digest mode.",
                durationMinutes = 5,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 7,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 0,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Ocean Waves",
                benefits = "Activates vagal brake to lower heart rate",
                downloadSizeMb = 11.8f
            ),
            Exercise(
                id = "coherent_5_5",
                title = "Resonant Coherence (5.5s)",
                category = "Stress Relief",
                description = "Breathe at optimal 5.5 breaths per minute for optimal cardiovascular synchrony and immediate stress decompression.",
                durationMinutes = 5,
                difficulty = "Beginner",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 0,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 0,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Forest Rain",
                benefits = "Optimizes heart rate variability (HRV) sync",
                downloadSizeMb = 13.5f
            ),
            Exercise(
                id = "deep_sleep_twilight",
                title = "Twilight Slumber Journey",
                category = "Sleep",
                description = "Extended guided calming session with gentle acoustic drone and elongated exhales to prepare your body for deep restorative sleep.",
                durationMinutes = 10,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 4,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = true,
                ambientSound = "Deep Drone",
                benefits = "Eases brain waves into soothing delta sleep rhythms",
                downloadSizeMb = 28.2f
            ),
            Exercise(
                id = "wim_hof_capacity",
                title = "Vitality & Lung Endurance",
                category = "Lung Capacity",
                description = "Deep rhythm cycles followed by brief retention to challenge thoracic elasticity and increase carbon dioxide tolerance.",
                durationMinutes = 8,
                difficulty = "Advanced",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 3,
                holdAfterExhaleSeconds = 8,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Forest Rain",
                benefits = "Improves CO2 threshold and oxygen delivery",
                downloadSizeMb = 18.6f
            ),
            Exercise(
                id = "alpha_focus",
                title = "Alpha Flow Focus",
                category = "Focus",
                description = "Crisp, balanced breath cadences to center attention, sharpen executive focus, and prepare for creative deep work.",
                durationMinutes = 4,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Sharpens cognitive alertness without agitation",
                downloadSizeMb = 10.2f
            ),
            Exercise(
                id = "ocean_calm_session",
                title = "Serene Waves Sanctuary",
                category = "Relaxation",
                description = "Immersive full calming session with synchronized tidal wave rhythm and breathing visuals for total somatic decompression.",
                durationMinutes = 12,
                difficulty = "Intermediate",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 3,
                exhaleSeconds = 7,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = true,
                ambientSound = "Ocean Waves",
                benefits = "Relieves somatic muscular tension throughout torso",
                downloadSizeMb = 32.0f
            ),
            Exercise(
                id = "nadi_shodhana",
                title = "Nadi Shodhana Channel Balance",
                category = "Relaxation",
                description = "Ancient yogic breath purification that harmonizes the left and right brain hemispheres, lowering stress and steadying emotions.",
                durationMinutes = 6,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 4,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Balances autonomic hemispheres and stabilizes emotional equilibrium",
                downloadSizeMb = 14.8f
            ),
            Exercise(
                id = "physiological_sigh",
                title = "Physiological Sigh (Neuro Reset)",
                category = "Stress Relief",
                description = "Neurobiology-backed instant reset protocol: double inhale to pop open collapsed alveoli followed by a long, sighing exhale.",
                durationMinutes = 3,
                difficulty = "Beginner",
                inhaleSeconds = 3,
                holdAfterInhaleSeconds = 1,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Ocean Waves",
                benefits = "Rapidly offloads excess CO2 and drops heart rate instantly",
                downloadSizeMb = 8.5f
            ),
            Exercise(
                id = "pranayama_ujjayi",
                title = "Pranayama Ujjayi (Ocean Whisper)",
                category = "Relaxation",
                description = "Gentle glottic resonance breath that warms thoracic airflow, stimulates vagal tone, and creates deep interior stillness.",
                durationMinutes = 7,
                difficulty = "Intermediate",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Ocean Waves",
                benefits = "Stimulates vagus nerve and deepens mental tranquility",
                downloadSizeMb = 16.4f
            ),
            Exercise(
                id = "kapalabhati_energy",
                title = "Kapalabhati (Skull Shining Breath)",
                category = "Focus",
                description = "Active diaphragmatic rhythmic pumping to invigorate cerebral blood flow, clear mental fog, and energize morning focus.",
                durationMinutes = 4,
                difficulty = "Advanced",
                inhaleSeconds = 2,
                holdAfterInhaleSeconds = 0,
                exhaleSeconds = 2,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Stimulates sympathetic alertness and clears respiratory pathways",
                downloadSizeMb = 9.8f
            ),
            Exercise(
                id = "deep_delta_sleep",
                title = "Deep Delta Sleep Wind-Down",
                category = "Sleep",
                description = "Hypnotic long-exhale protocol designed to slow brainwave frequencies from beta down to alpha and theta prior to falling asleep.",
                durationMinutes = 12,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = true,
                ambientSound = "Deep Drone",
                benefits = "Lowers core nervous tone and induces tranquil pre-sleep drowsiness",
                downloadSizeMb = 30.5f
            ),
            Exercise(
                id = "thoracic_expansion",
                title = "Thoracic Rib Cage Expansion",
                category = "Lung Capacity",
                description = "Targets intercostal muscles and chest wall elasticity to increase total lung capacity and combat shallow chest breathing.",
                durationMinutes = 6,
                difficulty = "Intermediate",
                inhaleSeconds = 6,
                holdAfterInhaleSeconds = 4,
                exhaleSeconds = 6,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Stretches rib cage musculature and enhances full vital capacity",
                downloadSizeMb = 15.6f
            ),
            Exercise(
                id = "hypoxic_conditioning",
                title = "High-Altitude Hypoxic Conditioning",
                category = "Lung Capacity",
                description = "Apnea retention training designed for athletes to enhance cellular efficiency, spleen contraction, and EPO stimulation.",
                durationMinutes = 8,
                difficulty = "Advanced",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 6,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 6,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Forest Rain",
                benefits = "Increases red blood cell delivery and CO2 breath-hold tolerance",
                downloadSizeMb = 19.2f
            ),
            Exercise(
                id = "midday_panic_melter",
                title = "Midday Tension & Panic Melter",
                category = "Stress Relief",
                description = "Quick-acting grounding sequence engineered to deactivate the acute fight-or-flight response during high-stress situations.",
                durationMinutes = 4,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 3,
                exhaleSeconds = 7,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Ocean Waves",
                benefits = "Dampens amygdala reactivity and restores composed steady breathing",
                downloadSizeMb = 11.2f
            ),
            Exercise(
                id = "morning_awakening",
                title = "Morning Awakening Energizer",
                category = "Focus",
                description = "Brisk, oxygen-rich breathing sequence to stimulate cortisol gently for natural morning energy without caffeine jitters.",
                durationMinutes = 5,
                difficulty = "Intermediate",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 0,
                exhaleSeconds = 2,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Boosts oxygen uptake and awakens muscular readiness",
                downloadSizeMb = 12.0f
            ),
            Exercise(
                id = "bhramari_humming",
                title = "Bhramari (Humming Bee Calm)",
                category = "Relaxation",
                description = "Gentle vibratory breathing with prolonged exhalation that dramatically boosts nasal nitric oxide production and soothes neural hyperactivity.",
                durationMinutes = 6,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 2,
                exhaleSeconds = 8,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = true,
                ambientSound = "Deep Drone",
                benefits = "Multiplies nasal nitric oxide release and relieves cranial tension",
                downloadSizeMb = 17.5f
            ),
            Exercise(
                id = "balance_harmony_717",
                title = "7-1-7 Deep Balance Harmony",
                category = "Stress Relief",
                description = "Extended symmetrical breathing that anchors emotional resilience and dissolves chronic postural tension.",
                durationMinutes = 7,
                difficulty = "Intermediate",
                inhaleSeconds = 7,
                holdAfterInhaleSeconds = 1,
                exhaleSeconds = 7,
                holdAfterExhaleSeconds = 1,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Forest Rain",
                benefits = "Stabilizes autonomic balance and promotes sustained equanimity",
                downloadSizeMb = 16.8f
            ),
            Exercise(
                id = "somno_release_body_scan",
                title = "Somno-Release Body Scan Breath",
                category = "Sleep",
                description = "Gentle rhythmic breathing synchronized with progressive physical release to quiet insomnia and racing thoughts before bed.",
                durationMinutes = 15,
                difficulty = "Beginner",
                inhaleSeconds = 4,
                holdAfterInhaleSeconds = 3,
                exhaleSeconds = 7,
                holdAfterExhaleSeconds = 3,
                isDownloaded = true,
                isCalmingSession = true,
                ambientSound = "Deep Drone",
                benefits = "Releases subconscious somatic tension and aids smooth sleep onset",
                downloadSizeMb = 34.0f
            ),
            Exercise(
                id = "buteyko_light_volume",
                title = "Buteyko Light Volume Breathing",
                category = "Lung Capacity",
                description = "Clinical technique emphasizing nasal, silent, reduced-volume breathing to reset chemoreceptor sensitivity and improve tissue oxygenation.",
                durationMinutes = 6,
                difficulty = "Intermediate",
                inhaleSeconds = 3,
                holdAfterInhaleSeconds = 1,
                exhaleSeconds = 4,
                holdAfterExhaleSeconds = 4,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Zen Stream",
                benefits = "Improves cellular oxygen absorption and eliminates chronic hyperventilation",
                downloadSizeMb = 14.0f
            ),
            Exercise(
                id = "executive_flow_state",
                title = "Executive Focus & Flow State",
                category = "Focus",
                description = "Structured rhythm designed to induce task immersion, synchronize theta-alpha brainwaves, and eliminate procrastination.",
                durationMinutes = 8,
                difficulty = "Intermediate",
                inhaleSeconds = 5,
                holdAfterInhaleSeconds = 3,
                exhaleSeconds = 5,
                holdAfterExhaleSeconds = 2,
                isDownloaded = true,
                isCalmingSession = false,
                ambientSound = "Forest Rain",
                benefits = "Enhances working memory retention and sustains uninterrupted concentration",
                downloadSizeMb = 18.0f
            )
        )
    }

    suspend fun toggleExerciseDownload(id: String, currentDownloaded: Boolean) {
        exerciseDao.setDownloaded(id, !currentDownloaded)
    }

    suspend fun setAllExercisesDownloaded(downloaded: Boolean) {
        exerciseDao.setAllDownloaded(downloaded)
    }

    suspend fun recordSession(
        exerciseId: String,
        title: String,
        category: String,
        durationSeconds: Int
    ): Long {
        val currentStreak = _settings.value.streakCount
        val updatedStreak = currentStreak + 1
        updateStreak(updatedStreak)

        return sessionDao.insertSession(
            BreathingSessionRecord(
                exerciseId = exerciseId,
                exerciseTitle = title,
                category = category,
                durationSeconds = durationSeconds,
                completedTimestamp = System.currentTimeMillis(),
                streakDayCount = updatedStreak
            )
        )
    }

    suspend fun recordLungTest(holdTimeSeconds: Int, capacityLiters: Float): LungTestRecord {
        val previous = lungTestDao.getLatestTestSync()
        val prevCapacity = previous?.capacityLiters ?: 4.0f
        val improvement = ((capacityLiters - prevCapacity) / prevCapacity) * 100f

        val record = LungTestRecord(
            timestamp = System.currentTimeMillis(),
            holdTimeSeconds = holdTimeSeconds,
            capacityLiters = capacityLiters,
            improvementPercent = improvement,
            note = if (improvement >= 0) "+${String.format("%.1f", improvement)}% vs previous test" else "${String.format("%.1f", improvement)}% vs previous test"
        )
        lungTestDao.insertTest(record)
        return record
    }

    suspend fun resetAllProgress() {
        sessionDao.clearAllSessions()
        lungTestDao.clearAllTests()
        updateStreak(0)
    }

    private fun loadSettings(): UserSettings {
        return UserSettings(
            dailyGoalMinutes = prefs.getInt("daily_goal", 10),
            defaultDurationMinutes = prefs.getInt("default_duration", 5),
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            soundVolume = prefs.getFloat("sound_volume", 0.6f),
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            animationStyle = prefs.getString("animation_style", "Pulsing Circle") ?: "Pulsing Circle",
            notificationsEnabled = prefs.getBoolean("notifications_enabled", true),
            offlineModeForced = prefs.getBoolean("offline_forced", false),
            streakCount = prefs.getInt("streak_count", 0)
        )
    }

    fun updateSettings(newSettings: UserSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putInt("daily_goal", newSettings.dailyGoalMinutes)
            .putInt("default_duration", newSettings.defaultDurationMinutes)
            .putBoolean("sound_enabled", newSettings.soundEnabled)
            .putFloat("sound_volume", newSettings.soundVolume)
            .putBoolean("haptic_enabled", newSettings.hapticEnabled)
            .putString("animation_style", newSettings.animationStyle)
            .putBoolean("notifications_enabled", newSettings.notificationsEnabled)
            .putBoolean("offline_forced", newSettings.offlineModeForced)
            .putInt("streak_count", newSettings.streakCount)
            .apply()
    }

    fun updateStreak(newStreak: Int) {
        val updated = _settings.value.copy(streakCount = newStreak)
        updateSettings(updated)
    }

    fun getMilestones(totalSessions: Int, totalMinutes: Int, streak: Int): List<Milestone> {
        return listOf(
            Milestone(
                id = "first_breath",
                title = "First Breath",
                description = "Complete your very first guided session",
                iconName = "spa",
                isUnlocked = totalSessions >= 1,
                progress = (totalSessions.coerceAtMost(1)) / 1.0f
            ),
            Milestone(
                id = "streak_3",
                title = "3-Day Zen",
                description = "Maintain a 3-day breathing streak",
                iconName = "local_fire_department",
                isUnlocked = streak >= 3,
                progress = (streak.coerceAtMost(3)) / 3.0f
            ),
            Milestone(
                id = "streak_7",
                title = "Consistent Lung",
                description = "Achieve a full 7-day wellness streak",
                iconName = "workspace_premium",
                isUnlocked = streak >= 7,
                progress = (streak.coerceAtMost(7)) / 7.0f
            ),
            Milestone(
                id = "capacity_builder",
                title = "Vital Capacity",
                description = "Complete 5 lung expansion exercises",
                iconName = "air",
                isUnlocked = totalSessions >= 5,
                progress = (totalSessions.coerceAtMost(5)) / 5.0f
            ),
            Milestone(
                id = "century_breather",
                title = "100 Breathing Minutes",
                description = "Spend over 100 minutes in conscious breathing",
                iconName = "timer",
                isUnlocked = totalMinutes >= 100,
                progress = (totalMinutes.coerceAtMost(100)) / 100.0f
            )
        )
    }
}
