package com.respira.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.respira.data.model.BreathingSessionRecord
import com.respira.data.model.Exercise
import com.respira.data.model.LungTestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY title ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: String): Exercise?

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY title ASC")
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Query("UPDATE exercises SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun setDownloaded(id: String, isDownloaded: Boolean)

    @Query("UPDATE exercises SET isDownloaded = :isDownloaded")
    suspend fun setAllDownloaded(isDownloaded: Boolean)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY completedTimestamp DESC")
    fun getAllSessions(): Flow<List<BreathingSessionRecord>>

    @Query("SELECT * FROM sessions ORDER BY completedTimestamp DESC LIMIT 5")
    fun getRecentSessions(): Flow<List<BreathingSessionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BreathingSessionRecord): Long

    @Query("SELECT SUM(durationSeconds) FROM sessions")
    fun getTotalBreathingSeconds(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("DELETE FROM sessions")
    suspend fun clearAllSessions()
}

@Dao
interface LungTestDao {
    @Query("SELECT * FROM lung_tests ORDER BY timestamp DESC")
    fun getAllTests(): Flow<List<LungTestRecord>>

    @Query("SELECT * FROM lung_tests ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTest(): Flow<LungTestRecord?>

    @Query("SELECT * FROM lung_tests ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTestSync(): LungTestRecord?

    @Query("SELECT * FROM lung_tests ORDER BY timestamp DESC LIMIT 7")
    fun getRecentTests(): Flow<List<LungTestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: LungTestRecord): Long

    @Query("DELETE FROM lung_tests")
    suspend fun clearAllTests()
}
