package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val occupation: String,
    val location: String,
    val bio: String,
    val interests: String, // Comma-separated
    val values: String, // Comma-separated
    val careerGoal: String,
    val familyGoal: String,
    val travelGoal: String,
    val membershipTier: String = "Essential", // Essential, Premium, Concierge
    val trustScore: Int = 85
)

@Entity(tableName = "curated_matches")
data class CuratedMatch(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val location: String,
    val bio: String,
    val photoUrl: String, // Descriptive name/ID of vector illustration
    val compatibilityScore: Int,
    val whyCompatible: String,
    val commonInterests: String, // Comma-separated
    val sharedValues: String, // Comma-separated
    val conversationStarters: String, // Comma-separated
    val aiIntroduction: String,
    val personalityGraphJson: String, // Simulating visual dimensions
    val lifestyleChoice: String,
    val futureAmbition: String,
    val isDailyIntroduction: Boolean = true,
    val isLiked: Boolean = false,
    val isMatched: Boolean = false
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: String,
    val sender: String, // "me" or matchName
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val isVideoIntro: Boolean = false
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // Dinner, Gallery, Book Club, Coffee, walking
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val imagePlaceholder: String,
    val isJoined: Boolean = false
)

@Entity(tableName = "coach_messages")
data class CoachMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AureliaDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM curated_matches")
    fun getCuratedMatches(): Flow<List<CuratedMatch>>

    @Query("SELECT * FROM curated_matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): CuratedMatch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<CuratedMatch>)

    @Query("UPDATE curated_matches SET isLiked = :liked WHERE id = :matchId")
    suspend fun updateLiked(matchId: String, liked: Boolean)

    @Query("UPDATE curated_matches SET isMatched = :matched WHERE id = :matchId")
    suspend fun updateMatched(matchId: String, matched: Boolean)

    @Query("SELECT * FROM messages WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getMessagesForMatch(matchId: String): Flow<List<Message>>

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getEvents(): Flow<List<Event>>

    @Query("UPDATE events SET isJoined = :joined WHERE id = :eventId")
    suspend fun updateJoinedEvent(eventId: String, joined: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Query("SELECT * FROM coach_messages ORDER BY timestamp ASC")
    fun getCoachMessages(): Flow<List<CoachMessage>>

    @Insert
    suspend fun insertCoachMessage(msg: CoachMessage)

    @Query("DELETE FROM coach_messages")
    suspend fun clearCoachMessages()
}
