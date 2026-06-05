package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserProfile::class, CuratedMatch::class, Message::class, Event::class, CoachMessage::class],
    version = 1,
    exportSchema = false
)
abstract class AureliaDatabase : RoomDatabase() {
    abstract fun dao(): AureliaDao

    companion object {
        @Volatile
        private var INSTANCE: AureliaDatabase? = null

        fun getInstance(context: Context): AureliaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AureliaDatabase::class.java,
                    "aurelia_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed database in coroutine
                        CoroutineScope(Dispatchers.IO).launch {
                            seedData(context)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedData(context: Context) {
            val db = getInstance(context)
            val dao = db.dao()

            // Check if matches are already seeded
            val currentMatches = dao.getCuratedMatches()
            // We can check if any exists
            val existing = dao.getUserProfileSync()
            if (existing == null) {
                // Seed a profile with default values, which can be custom configured by user in profile editor
                dao.saveUserProfile(
                    UserProfile(
                        name = "Thomas",
                        age = 31,
                        occupation = "Creative Director",
                        location = "London",
                        bio = "Always looking for architectural details in the corners of ancient streets. Passionate about minimalism, print design, and high-altitude hiking.",
                        interests = "Design, Architecture, Hiking, Espressos, Print Media",
                        values = "Aesthetics, Authenticity, Lifelong Learning, Intellectual Curiosity",
                        careerGoal = "Establishing an independent editorial house",
                        familyGoal = "Stable companion and visual spaces",
                        travelGoal = "Visiting Nordic architectural sites"
                    )
                )

                // Seed upscale matches
                val matches = listOf(
                    CuratedMatch(
                        id = "julian",
                        name = "Julian Harris",
                        age = 32,
                        occupation = "Brutalist Architect",
                        location = "London",
                        bio = "Passionate about structural design and sustainable spaces. Spends weekends sketching brutalist towers, long-distance hiking, and brewing micro-batch single-origin espresso. Believes that physical spaces shape our soul.",
                        photoUrl = "vector_julian",
                        compatibilityScore = 93,
                        whyCompatible = "Both share a deep appreciation for geometric integrity, design-centric travels, and unhurried weekend hikes.",
                        commonInterests = "Architecture, Hiking, Design, Single-origin Espresso",
                        sharedValues = "Aesthetics, Sustainable Design, Authenticity",
                        conversationStarters = "What is your absolute favorite brutalist building in London?,Have you ever tried drawing as a form of meditation?",
                        aiIntroduction = "Julian and you are both highly design-oriented and enjoy sketching or exploring architectural history during long-distance walking excursions.",
                        personalityGraphJson = "{\"Personality\": 90, \"Values\": 94, \"Lifestyle\": 88, \"Ambition\": 95, \"Emotional Intelligence\": 92}",
                        lifestyleChoice = "Symmetric, slow-living",
                        futureAmbition = "Designing an eco-friendly residential pavilion in Switzerland."
                    ),
                    CuratedMatch(
                        id = "clara",
                        name = "Clara Moretti",
                        age = 29,
                        occupation = "Gallery Curator & Pianist",
                        location = "Chelsea, London",
                        bio = "Curator of contemporary European exhibits by day, performance pianist by heart. Believes digital platforms should match the grace of physical encounters. Speaks three languages and seeks deep literary dialogue.",
                        photoUrl = "vector_clara",
                        compatibilityScore = 95,
                        whyCompatible = "Perfect alignment on creative pursuits (95%) and shared pursuit of literary elegance. Strong communication patterns.",
                        commonInterests = "Fine Art Galleries, Classical Music, Travel, Culinary Arts",
                        sharedValues = "Creative Passion, Intellect, Culinary Care",
                        conversationStarters = "If you could curate a double-exhibition of any two historical artists, who would they be?,What classical piece instantly grounds you after a turbulent day?",
                        aiIntroduction = "Elena and Clara both share a deep connection to modern curation, appreciate unhurried culinary curation, and believe in slower, intentional digital boundaries.",
                        personalityGraphJson = "{\"Personality\": 96, \"Values\": 92, \"Lifestyle\": 94, \"Ambition\": 91, \"Emotional Intelligence\": 97}",
                        lifestyleChoice = "Curated luxury, late galleries",
                        futureAmbition = "Opening a co-operative artist residence in Florence."
                    ),
                    CuratedMatch(
                        id = "marcus",
                        name = "Dr. Marcus Vance",
                        age = 35,
                        occupation = "Cognitive Neuroscientist",
                        location = "Oxford / London",
                        bio = "Researching cognitive maps during the day, writing historical fiction at night. Seeks a partner who values quiet reading hours, slow coffee outings, intellectual debate, and slow travel.",
                        photoUrl = "vector_marcus",
                        compatibilityScore = 89,
                        whyCompatible = "High overlap in values and intellectual exploration (94%). You both enjoy a quiet, slow lifestyle centered around books.",
                        commonInterests = "Literature, Specialty Coffee, Neuroscience, Historical Cities",
                        sharedValues = "Intellectual Curiosity, Quiet Self-trust, Open Dialogue",
                        conversationStarters = "If memory could be visually stored as a gallery, what would be your entry-hall piece?,Are you more drawn to the sensory detail of a city or its historical lore?",
                        aiIntroduction = "Marcus and you share a profound neurological curiosity. You are both drawn to deep libraries, slow-flowing filter coffees, and independent bookselling vaults.",
                        personalityGraphJson = "{\"Personality\": 88, \"Values\": 95, \"Lifestyle\": 85, \"Ambition\": 90, \"Emotional Intelligence\": 89}",
                        lifestyleChoice = "Thoughtful, academic pacing",
                        futureAmbition = "Publishing a speculative novel about consciousness and art."
                    ),
                    CuratedMatch(
                        id = "elena",
                        name = "Elena Rostova",
                        age = 31,
                        occupation = "Art Agency Founder",
                        location = "Kensington, London",
                        bio = "Founded an independent fine-art consulting firm representing rising sculptors. Active in local charities. Loves coastal sailing and playing the violin on winter evenings.",
                        photoUrl = "vector_elena",
                        compatibilityScore = 91,
                        whyCompatible = "Excellent ambition synergy (93%). You both share a vision for aesthetic patronage and sustainable independence.",
                        commonInterests = "Sculpting, Coast Yachting, Violin, Philanthropy",
                        sharedValues = "Aesthetic Preservation, Altruism, Self-Reliance",
                        conversationStarters = "What sculpture style has resonated with you recently?,Do you prefer the chaos of a launching harbor or the silent horizon?",
                        aiIntroduction = "Both you and Elena have an affinity for coastal sea breezes, fine string music, and the delicate patience required to discover rising artistic talents.",
                        personalityGraphJson = "{\"Personality\": 92, \"Values\": 91, \"Lifestyle\": 93, \"Ambition\": 94, \"Emotional Intelligence\": 91}",
                        lifestyleChoice = "Vigorous, cultural involvement",
                        futureAmbition = "Erecting a permanent public art park along the Devon coast."
                    )
                )
                dao.insertMatches(matches)

                // Seed luxury social events
                val events = listOf(
                    Event(
                        id = "event_dinner",
                        title = "Candlelight Dinner & Dialogue",
                        type = "Dinner",
                        date = "2026-06-20",
                        time = "19:30",
                        location = "The Club Room, Mayfair",
                        description = "An intimate gathering of 10 selected Aurelia members in a private salon. Dinner includes a custom-designed tasting menu. All devices are checked at the door; conversation is guided by deep topic cards centered around architectural preservation, aesthetic philosophy, and future visions.",
                        imagePlaceholder = "ic_dinner"
                    ),
                    Event(
                        id = "event_gallery",
                        title = "Modernist Sculpture Private Showing",
                        type = "Gallery",
                        date = "2026-06-25",
                        time = "18:30",
                        location = "Tate Britain Terrace Room",
                        description = "An exclusive after-hours viewing of modernist stone and metal sculptures. Enjoy champagne on the terrace overlooking the Thames with architectural curators and other refined creative minds.",
                        imagePlaceholder = "ic_gallery"
                    ),
                    Event(
                        id = "event_salon",
                        title = "Literary Salon: Aesthetics of Intimacy",
                        type = "Book Club",
                        date = "2026-07-02",
                        time = "19:00",
                        location = "The Library Vault, Soho House",
                        description = "A wine and literary discussion focused on European essays and the architecture of relationships in our modern digital epoch. Members are encouraged to bring an essay or a poem that has reoriented their perspective on connection.",
                        imagePlaceholder = "ic_salon"
                    )
                )
                dao.insertEvents(events)
            }
        }
    }
}
