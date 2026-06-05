package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.Ivory
import com.example.ui.theme.LightGold
import com.example.ui.theme.SoftGold
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.WarmGrey
import com.example.ui.theme.WarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    // Step 1: Basic specifications
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("28") }
    var occupation by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("London") }

    // Step 2: Storytelling
    var bio by remember { mutableStateOf("") }

    // Step 3: Values & Interests
    val selectedInterests = remember { mutableStateListOf("Architecture", "Design", "Single-origin Espresso") }
    val selectedValues = remember { mutableStateListOf("Aesthetics", "Intellect", "Authenticity") }

    // Step 4: Goals & Tiers
    var careerGoal by remember { mutableStateOf("Establishing an independent creative agency") }
    var familyGoal by remember { mutableStateOf("A stable companion sharing coastal values") }
    var travelGoal by remember { mutableStateOf("Architectural reviews of modern European pavilions") }
    var membershipTier by remember { mutableStateOf("Essential") } // Essential, Premium, Concierge

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        containerColor = WarmWhite // Onboarding uses light mode to convey warm European paper aesthetic
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Elegant progress layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A U R E L I A",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepNavy.copy(alpha = 0.5f),
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.width(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(if (i <= step) SoftGold else WarmGrey)
                        )
                    }
                }
            }

            // Animated Form Steps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                when (step) {
                    1 -> OnboardingStep1(
                        name = name,
                        onNameChange = { name = it },
                        age = ageStr,
                        onAgeChange = { ageStr = it },
                        occupation = occupation,
                        onOccupationChange = { occupation = it },
                        location = location,
                        onLocationChange = { location = it }
                    )
                    2 -> OnboardingStep2(
                        bio = bio,
                        onBioChange = { bio = it }
                    )
                    3 -> OnboardingStep3(
                        selectedInterests = selectedInterests,
                        selectedValues = selectedValues
                    )
                    4 -> OnboardingStep4(
                        career = careerGoal,
                        onCareerChange = { careerGoal = it },
                        family = familyGoal,
                        onFamilyChange = { familyGoal = it },
                        travel = travelGoal,
                        onTravelChange = { travelGoal = it },
                        selectedTier = membershipTier,
                        onTierChange = { membershipTier = it }
                    )
                }
            }

            // Step Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    Text(
                        text = "BACK",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepNavy.copy(alpha = 0.6f),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { step-- }
                            .padding(12.dp)
                            .testTag("onboarding_back")
                    )
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            // Validation mock
                            if (step == 1 && name.isBlank()) {
                                name = "Thomas" // Fallback name
                            }
                            if (step == 1 && occupation.isBlank()) {
                                occupation = "Creative Director" // Fallback occupation
                            }
                            if (step == 2 && bio.isBlank()) {
                                bio = "Deeply curious about brutalist architecture and fine-art curation. Looking for organic, screen-free weekend dialogs."
                            }
                            step++
                        } else {
                            // Finalize profiles
                            val finalAge = ageStr.toIntOrNull() ?: 30
                            val profile = UserProfile(
                                name = name.ifBlank { "Thomas" },
                                age = finalAge,
                                occupation = occupation.ifBlank { "Creative Director" },
                                location = location.ifBlank { "London" },
                                bio = bio.ifBlank { "Refined aesthetician focusing on design limits and slow conversational depth." },
                                interests = selectedInterests.joinToString(","),
                                values = selectedValues.joinToString(","),
                                careerGoal = careerGoal,
                                familyGoal = familyGoal,
                                travelGoal = travelGoal,
                                membershipTier = membershipTier
                            )
                            onComplete(profile)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepNavy,
                        contentColor = WarmWhite
                    ),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("onboarding_next")
                ) {
                    Text(
                        text = if (step == 4) "ACTIVATE AURELIA" else "CONTINUE",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingStep1(
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    occupation: String,
    onOccupationChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Personal Specifications",
            style = MaterialTheme.typography.displayMedium,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Initiate your Aurelia residency. We prioritize adult transparency over digital aliases.",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp),
            lineHeight = 20.sp
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("What is your full name?", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Thomas", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftGold,
                unfocusedBorderColor = CoolGreyBorder,
                focusedLabelColor = DeepNavy
            )
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text("Age") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftGold,
                    unfocusedBorderColor = CoolGreyBorder,
                    focusedLabelColor = DeepNavy
                )
            )

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("City of Residence") },
                singleLine = true,
                modifier = Modifier.weight(2.0f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftGold,
                    unfocusedBorderColor = CoolGreyBorder,
                    focusedLabelColor = DeepNavy
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = occupation,
            onValueChange = onOccupationChange,
            label = { Text("What is your professional occupation?", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Artistic Entrepreneur / Editorial Director", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftGold,
                unfocusedBorderColor = CoolGreyBorder,
                focusedLabelColor = DeepNavy
            )
        )
    }
}

@Composable
fun OnboardingStep2(
    bio: String,
    onBioChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Storytelling Format",
            style = MaterialTheme.typography.displayMedium,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Describe your physical or creative snapshots in high-fidelity prose. Skip bullet points. Build an ecosystem.",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp),
            lineHeight = 20.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftGold.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Aurelia Bio Prompt Idea:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "\"Spends weeks exploring geometric concrete arches and cataloging vintage magazines. Seeks a partner with similar cognitive architecture.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            }
        }

        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange,
            label = { Text("Your Narrative Biography", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Start sketching your story detail...", color = Color.Gray) },
            minLines = 4,
            maxLines = 8,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bio_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftGold,
                unfocusedBorderColor = CoolGreyBorder,
                focusedLabelColor = DeepNavy
            )
        )
    }
}

@Composable
fun OnboardingStep3(
    selectedInterests: MutableList<String>,
    selectedValues: MutableList<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Aesthetic Coherence",
            style = MaterialTheme.typography.displayMedium,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select values and niche cultural markers that anchor your relationship goals.",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp),
            lineHeight = 18.sp
        )

        // Values selector
        Text(
            text = "CORE MORAL COMPASS VALUES",
            style = MaterialTheme.typography.labelMedium,
            color = SoftGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val valuesOptions = listOf("Aesthetics", "Intellect", "Authenticity", "Quiet Rest", "Philanthropy", "Creative Legacy")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                valuesOptions.chunked(3).forEach { chunk ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunk.forEach { value ->
                            val isSelected = selectedValues.contains(value)
                            Box(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        if (isSelected) SoftGold else CoolGreyBorder,
                                        RoundedCornerShape(2.dp)
                                    )
                                    .background(if (isSelected) DeepNavy else Color.Transparent)
                                    .clickable {
                                        if (isSelected) selectedValues.remove(value) else selectedValues.add(value)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) WarmWhite else DeepNavy,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interests selector
        Text(
            text = "CULTURAL INTERESTS & NICHE MARKERS",
            style = MaterialTheme.typography.labelMedium,
            color = SoftGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val interestsOptions = listOf("Architecture", "Fine Arts", "Chess", "Classical Music", "Coast Sailing", "Single-origin Espresso", "Literature", "Symmetric Photography")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            interestsOptions.chunked(2).forEach { chunk ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunk.forEach { interest ->
                        val isSelected = selectedInterests.contains(interest)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isSelected) SoftGold else CoolGreyBorder,
                                    RoundedCornerShape(2.dp)
                                )
                                .background(if (isSelected) DeepNavy else Color.Transparent)
                                .clickable {
                                    if (isSelected) selectedInterests.remove(interest) else selectedInterests.add(interest)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
                                        tint = SoftGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = interest,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) WarmWhite else DeepNavy,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingStep4(
    career: String,
    onCareerChange: (String) -> Unit,
    family: String,
    onFamilyChange: (String) -> Unit,
    travel: String,
    onTravelChange: (String) -> Unit,
    selectedTier: String,
    onTierChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Future Dimensions",
            style = MaterialTheme.typography.displayMedium,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Set alignment expectations. Aurelia members coordinate legacy goals transparently.",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 20.dp),
            lineHeight = 20.sp
        )

        OutlinedTextField(
            value = career,
            onValueChange = onCareerChange,
            label = { Text("What is your primary Career trajectory?") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftGold,
                unfocusedBorderColor = CoolGreyBorder,
                focusedLabelColor = DeepNavy
            )
        )

        OutlinedTextField(
            value = family,
            onValueChange = onCareerChange, // Keep synchronized or target parameter
            label = { Text("What is your Family / Partner vision?") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftGold,
                unfocusedBorderColor = CoolGreyBorder,
                focusedLabelColor = DeepNavy
            )
        )

        // Membership Selection Tiers
        Text(
            text = "CHOOSE MEMBERSHIP RESIDENCY TIER",
            style = MaterialTheme.typography.labelMedium,
            color = SoftGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val tiers = listOf(
            Triple("Essential", "Core matching, 5 daily introductions.", "Standard inclusion"),
            Triple("Premium", "Advanced matching insights & expert coach.", "£25 / month"),
            Triple("Concierge", "Human matchmaking & private VIP salons.", "£250 / month")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tiers.forEach { (name, desc, cost) ->
                val isSelected = selectedTier == name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isSelected) SoftGold else CoolGreyBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onTierChange(name) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) DeepNavy else WarmWhite
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) SoftGold else DeepNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = cost,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) WarmWhite.copy(alpha = 0.8f) else DeepNavy.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) WarmWhite.copy(alpha = 0.8f) else DeepNavy.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// Global style border token
val CoolGreyBorder = Color(0xFFE2E2E2)
