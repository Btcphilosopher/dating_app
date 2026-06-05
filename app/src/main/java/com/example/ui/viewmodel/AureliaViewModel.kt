package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiService
import com.example.data.AureliaDatabase
import com.example.data.CoachMessage
import com.example.data.CuratedMatch
import com.example.data.Event
import com.example.data.Message
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AureliaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AureliaDatabase.getInstance(application)
    private val dao = db.dao()

    // Flow representing current onboarding/user profile
    val userProfile: StateFlow<UserProfile?> = dao.getUserProfile().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Flow of daily curated matches
    val curatedMatches: StateFlow<List<CuratedMatch>> = dao.getCuratedMatches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Flow of events
    val events: StateFlow<List<Event>> = dao.getEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Flow of AI Coach messages
    val coachMessages: StateFlow<List<CoachMessage>> = dao.getCoachMessages().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Track active match being viewed in detailed card view (null if none, otherwise id)
    private val _selectedMatchId = MutableStateFlow<String?>(null)
    val selectedMatchId = _selectedMatchId.asStateFlow()

    // Track active match chat screen
    private val _activeChatMatchId = MutableStateFlow<String?>(null)
    val activeChatMatchId = _activeChatMatchId.asStateFlow()

    // State showing "It's a Match!" cinematic notification dialog
    private val _newlyMatchedName = MutableStateFlow<String?>(null)
    val newlyMatchedName = _newlyMatchedName.asStateFlow()

    init {
        // Clear coach messages but seed a friendly greeting upon boot if empty
        viewModelScope.launch {
            dao.getCoachMessages().collect { msgs ->
                if (msgs.isEmpty()) {
                    dao.insertCoachMessage(
                        CoachMessage(
                            role = "model",
                            content = "Warm greetings. I am your Aurelia Relationship Coach. I map visual profiles, emotional habits, and alignment metrics. Ask me anything, or run an assessment on your profile description."
                        )
                    )
                }
            }
        }
    }

    fun selectMatch(id: String?) {
        _selectedMatchId.value = id
    }

    fun enterChat(id: String?) {
        _activeChatMatchId.value = id
    }

    fun clearNewlyMatched() {
        _newlyMatchedName.value = null
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            dao.saveUserProfile(profile)
        }
    }

    fun likeMatch(matchId: String, isLiked: Boolean) {
        viewModelScope.launch {
            dao.updateLiked(matchId, isLiked)
            val match = dao.getMatchById(matchId)
            if (isLiked && match != null && !match.isMatched) {
                // Instantly generate a mutual match scenario & prompt dialog
                dao.updateMatched(matchId, true)
                _newlyMatchedName.value = match.name

                // Auto-generate deep introductory messages or voice note mock
                dao.insertMessage(
                    Message(
                        matchId = matchId,
                        sender = "system",
                        content = "You and ${match.name} have formed a compatibility link (${match.compatibilityScore}% Compatibility score). AI introduction is active.",
                        timestamp = System.currentTimeMillis() - 1000
                    )
                )
                dao.insertMessage(
                    Message(
                        matchId = matchId,
                        sender = match.name,
                        content = match.aiIntroduction,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun getMessagesFor(matchId: String) = dao.getMessagesForMatch(matchId)

    fun sendMessageTo(matchId: String, text: String, isVoice: Boolean = false, duration: Int = 0) {
        viewModelScope.launch {
            dao.insertMessage(
                Message(
                    matchId = matchId,
                    sender = "me",
                    content = text,
                    isVoiceNote = isVoice,
                    voiceDurationSec = duration
                )
            )

            // Simulate match response after a brief delay
            val match = dao.getMatchById(matchId)
            if (match != null && !isVoice) {
                kotlinx.coroutines.delay(1500)
                val responseContent = when {
                    text.contains("favorite", ignoreCase = true) -> {
                        "That's a fascinating choice. For me, it's the Barbican Estate. The harmony of rough-cast concrete, vertical green gardens, and the elevated walkways creates a quiet sanctuary. Do you prefer brutalism during autumn or spring?"
                    }
                    text.contains("hello", ignoreCase = true) || text.contains("hi", ignoreCase = true) -> {
                        "Hello! I was reflecting on our AI compatibility intro. It is rare to meet somebody with similar design milestones. Tell me about your current creative work."
                    }
                    else -> {
                        "Your thoughts resonate deeply with me. It is beautiful how unhurried digital conversations reveal core layers. Let's arrange a walk this weekend near the Tate Britain."
                    }
                }
                dao.insertMessage(
                    Message(
                        matchId = matchId,
                        sender = match.name,
                        content = responseContent
                    )
                )
            }
        }
    }

    fun toggleEventRSVP(eventId: String, isJoined: Boolean) {
        viewModelScope.launch {
            dao.updateJoinedEvent(eventId, isJoined)
        }
    }

    private val _isCoachGenerating = MutableStateFlow(false)
    val isCoachGenerating = _isCoachGenerating.asStateFlow()

    fun sendCoachMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            // Log user message
            val userMsg = CoachMessage(role = "user", content = userText)
            dao.insertCoachMessage(userMsg)

            _isCoachGenerating.value = true

            // Gather conversation context
            val currentHist = dao.getCoachMessages().stateIn(viewModelScope).value
            val mappedHist = currentHist.map { it.role to it.content }

            // Query api
            val replyText = GeminiService.getCoachResponse(mappedHist, userText)

            val replyMsg = CoachMessage(role = "model", content = replyText)
            dao.insertCoachMessage(replyMsg)

            _isCoachGenerating.value = false
        }
    }

    fun clearCoachHistory() {
        viewModelScope.launch {
            dao.clearCoachMessages()
            dao.insertCoachMessage(
                CoachMessage(
                    role = "model",
                    content = "History cleared. How can I optimize your conversational pacing or review your profile strategy today?"
                )
            )
        }
    }
}

class AureliaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AureliaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AureliaViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
