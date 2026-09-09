package com.respira.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // Breathing Exercises, Lung Capacity, Relaxation, Stress Relief, Sleep, Focus
    val description: String,
    val durationMinutes: Int,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val isDownloaded: Boolean = true,
    val isCalmingSession: Boolean = false,
    val ambientSound: String = "Ocean Waves",
    val benefits: String = "Improves autonomic balance and oxygen saturation",
    val downloadSizeMb: Float = 14.2f
)

@Entity(tableName = "sessions")
data class BreathingSessionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseTitle: String,
    val category: String,
    val durationSeconds: Int,
    val completedTimestamp: Long = System.currentTimeMillis(),
    val streakDayCount: Int = 1
)

@Entity(tableName = "lung_tests")
data class LungTestRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val holdTimeSeconds: Int,
    val capacityLiters: Float,
    val improvementPercent: Float,
    val note: String = "Normal breathing baseline"
)

data class UserSettings(
    val dailyGoalMinutes: Int = 10,
    val defaultDurationMinutes: Int = 5,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 0.6f,
    val hapticEnabled: Boolean = true,
    val animationStyle: String = "Pulsing Circle", // "Pulsing Circle", "Expanding Lotus", "Calm Waves"
    val notificationsEnabled: Boolean = true,
    val offlineModeForced: Boolean = false,
    val streakCount: Int = 7
)

data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val progress: Float = 1.0f
)
