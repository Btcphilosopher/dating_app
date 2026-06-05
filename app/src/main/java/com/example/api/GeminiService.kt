package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Checks if a valid-looking API key is configured.
     */
    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrEmpty() && key != "MY_GEMINI_API_KEY" && key != "placeholder"
    }

    /**
     * Sends a chat prompt to Gemini. Supports conversational history formatted as contents array.
     */
    suspend fun getCoachResponse(history: List<Pair<String, String>>, userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!isApiKeyConfigured()) {
            Log.w(TAG, "Gemini API key is not configured. Falling back to high-quality template advice.")
            return@withContext getOfflineCoachFallback(userMessage)
        }

        try {
            // Build the conversational request body using JSONObject
            val root = JSONObject()
            val contentsArray = JSONArray()

            // System instructions (Optional but improves roleplay)
            val systemInstruction = JSONObject()
            val systemParts = JSONArray()
            systemParts.put(JSONObject().put("text", "You are Aurelia's premium AI Relationship Coach. You adopt an elite, warm, intelligent persona matching 'Financial Times meets Soho House'. Give professional, deep, compassionate dating guidance, profile optimizations, and emotional intelligence improvements. Avoid generic advice, speak with refined, adult clarity, and keep replies elegant and concise (1-2 paragraphs)."))
            systemInstruction.put("parts", systemParts)
            root.put("systemInstruction", systemInstruction)

            // Add previous message turns
            history.forEach { (role, txt) ->
                val contentObj = JSONObject()
                contentObj.put("role", if (role == "user") "user" else "model")
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", txt))
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            // Add current message
            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", userMessage))
            currentTurn.put("parts", currentParts)
            contentsArray.put(currentTurn)

            root.put("contents", contentsArray)

            // Optional temperature config
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            root.put("generationConfig", generationConfig)

            val requestBodyJson = root.toString()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyJson.toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errorStr = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API Call failed: $code $errorStr")
                    return@withContext "I am having difficulty syncing with the Aurelia network (Code $code). However, let us focus on what truly matters: slow, deliberate relationship parameters. Try configuring your Google AI Studio Secret Key in the side panel."
                }

                val responseBody = response.body?.string() ?: return@withContext "Empty response."
                val responseJson = JSONObject(responseBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No text segment found.")
                    }
                }
                return@withContext "Warm greetings. The cosmic architecture is peaceful, but no response was echoed. How may I best support your relationship journey today?"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getCoachResponse", e)
            return@withContext "My apologies. Connection to the matching servers was briefly interrupted (${e.localizedMessage}). Let us reflect quietly on your intention for dating: are you seeking proximity, or deep structural values similarity?"
        }
    }

    /**
     * Beautiful offline advisor templates for a high-end experience when no API Key is set up.
     */
    private fun getOfflineCoachFallback(userMessage: String): String {
        val query = userMessage.lowercase()
        return when {
            query.contains("profile") || query.contains("bio") || query.contains("optimization") -> {
                "**Aurelia Profile Architecture Strategy:**\n\nYour profile should read like a feature article in the *New Yorker* or a *Soho House* introduction, rather than a checklist. Avoid generic descriptors like 'love traveling' or 'active life.' Instead, describe *structural snapshots*:\n\n1. Seek **visual imagery**: Instead of 'I like coffee,' style it as 'brewing micro-batch single-origin espresso at 7:00 AM while sketching brutalist geometries.'\n2. Share **vulnerability with dignity**: Disclose one long-term ambition that frightens you slightly, such as 'establishing an organic artist cooperative along the Italian coast.'\n\nWould you like me to rewrite or audit your current Aurelia bio?"
            }
            query.contains("icebreaker") || query.contains("initiate") || query.contains("intro") || query.contains("starter") -> {
                "**Design-Driven Conversation Starters:**\n\nTo move beyond 'How is your week going?', initiate dialogue around **aesthetic parameters** or **lifestyle choices**:\n\n*   *'I see we both have a soft spot for architectural walks. If London's history were condensed into a single library, which room would you search first?'*\n*   *'You mentioned a passion for modern design. Do you find comfort in minimalist symmetry, or do you prefer the warm, lived-in chaos of classic European spaces?'*\n\nTry sending one of these to Julian or Clara today. It honors their intellect immediately."
            }
            query.contains("anxious") || query.contains("ghost") || query.contains("exhausted") || query.contains("swipe") -> {
                "**Dating Pacing & Cognitive Grounding:**\n\nModern platforms weaponize dopamine loops, causing relational fatigue and anxiety. Aurelia operates differently. If you are feeling overwhelmed, remember:\n\n1. **Primacy of Slower Cadence**: You are only shown 5-10 introductions per day. There is no endless supply of faces. Close the application after reviewing these, and let them settle into your subconscious.\n2. **The 48-Hour Interval**: If a curated match does not reply immediately, understand that adults leading complex, ambitious careers (artists, neuroscientists, founders) respect deep hours of work. Allow them space. True compatibility exists in spacious trust, not instantaneous ping-pong messaging."
            }
            else -> {
                "**Welcome to Aurelia Counsel:**\n\nI am your AI Relationship Coach, designed to cultivate emotional intelligence, profile clarity, and genuine connection. I pair modern psychological principles with Soho House's refined boundaries.\n\n*Currently operating in Offline Mode. To activate live cognitive intelligence, configure your `GEMINI_API_KEY` in the AI Studio Secrets panel.*\n\nHow can I support your journey? Ask me about **Profile Optimization**, **Sophisticated Icebreakers**, or how to navigate **Relational Fatigue**."
            }
        }
    }
}
