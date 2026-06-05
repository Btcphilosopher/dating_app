package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CuratedMatch
import com.example.data.Event
import com.example.data.Message
import com.example.data.UserProfile
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Ivory
import com.example.ui.theme.LightGold
import com.example.ui.theme.MutedNavy
import com.example.ui.theme.SoftGold
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.WarmGrey
import com.example.ui.theme.WarmWhite
import com.example.ui.viewmodel.AureliaViewModel
import org.json.JSONObject

enum class AureliaTab {
    Curated,
    Chats,
    Events,
    Coach,
    Insights
}

@Composable
fun HomeScreen(
    viewModel: AureliaViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(AureliaTab.Curated) }

    val userProfile by viewModel.userProfile.collectAsState()
    val matches by viewModel.curatedMatches.collectAsState()
    val events by viewModel.events.collectAsState()
    val activeChatMatchId by viewModel.activeChatMatchId.collectAsState()
    val newlyMatchedName by viewModel.newlyMatchedName.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        containerColor = DeepNavy,
        bottomBar = {
            AureliaBottomNavigation(
                activeTab = activeTab,
                onTabSelected = {
                    activeTab = it
                    viewModel.enterChat(null) // Reset active chat focus when changing tabs
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main views based on tab selection
            when {
                activeChatMatchId != null -> {
                    // Exclusive message thread layout
                    val currentMatch = matches.find { it.id == activeChatMatchId }
                    if (currentMatch != null) {
                        ChatRoomScreen(
                            match = currentMatch,
                            viewModel = viewModel,
                            onBack = { viewModel.enterChat(null) }
                        )
                    } else {
                        viewModel.enterChat(null)
                    }
                }
                else -> {
                    when (activeTab) {
                        AureliaTab.Curated -> CuratedTabScreen(
                            userProfile = userProfile,
                            matches = matches,
                            viewModel = viewModel,
                            onSignOut = onSignOut
                        )
                        AureliaTab.Chats -> ChatsTabScreen(
                            matches = matches,
                            viewModel = viewModel
                        )
                        AureliaTab.Events -> EventsTabScreen(
                            events = events,
                            viewModel = viewModel
                        )
                        AureliaTab.Coach -> CoachTabScreen(
                            viewModel = viewModel
                        )
                        AureliaTab.Insights -> InsightsTabScreen(
                            userProfile = userProfile,
                            viewModel = viewModel,
                            onSignOut = onSignOut
                        )
                    }
                }
            }

            // High-fidelity cinematic "It's a Match!" dialog overlay
            newlyMatchedName?.let { partnerName ->
                CinematicMatchDialog(
                    partnerName = partnerName,
                    onDismiss = { viewModel.clearNewlyMatched() },
                    onNavigateToChat = {
                        val matchingId = matches.find { it.name == partnerName }?.id
                        viewModel.clearNewlyMatched()
                        if (matchingId != null) {
                            viewModel.enterChat(matchingId)
                        } else {
                            activeTab = AureliaTab.Chats
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AureliaBottomNavigation(
    activeTab: AureliaTab,
    onTabSelected: (AureliaTab) -> Unit
) {
    NavigationBar(
        containerColor = Charcoal,
        modifier = Modifier.height(64.dp),
        windowInsets = androidx.compose.material3.NavigationBarDefaults.windowInsets
    ) {
        val items = listOf(
            Triple(AureliaTab.Curated, Icons.Default.Favorite, "Curated"),
            Triple(AureliaTab.Chats, Icons.Default.Email, "Messages"),
            Triple(AureliaTab.Events, Icons.Default.DateRange, "Salons"),
            Triple(AureliaTab.Coach, Icons.Default.Face, "Coach"),
            Triple(AureliaTab.Insights, Icons.Default.AccountCircle, "Insights")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = activeTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) SoftGold else Ivory.copy(alpha = 0.4f)
                    )
                },
                label = {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) SoftGold else Ivory.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MutedNavy
                )
            )
        }
    }
}

// ==================== CURATED TAB SCREEN ====================
@Composable
fun CuratedTabScreen(
    userProfile: UserProfile?,
    matches: List<CuratedMatch>,
    viewModel: AureliaViewModel,
    onSignOut: () -> Unit
) {
    // Curated matched lists
    val nonLikedMatches = matches.filter { !it.isLiked }
    val activeMatch = nonLikedMatches.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Labeling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AURELIA INTRODUCTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "Today's Curated Cohort",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ivory,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .border(1.dp, SoftGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${nonLikedMatches.size} Left",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightGold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeMatch != null) {
            // Curated match is presented in card view like a premium magazine feature
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .border(0.5.dp, SoftGold.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    .background(Charcoal)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Compatibility header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = SoftGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${activeMatch.compatibilityScore}% COMPATIBILITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "MEMBERSHIP VERIFIED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ivory.copy(alpha = 0.4f),
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Canvas Profile Artwork representing the person
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MutedNavy),
                    contentAlignment = Alignment.Center
                ) {
                    GeometricProfileVisual(
                        matchId = activeMatch.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${activeMatch.name}, ${activeMatch.age}",
                    style = MaterialTheme.typography.displayMedium,
                    color = WarmWhite,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = activeMatch.occupation,
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Text(
                    text = activeMatch.location.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ivory.copy(alpha = 0.5f),
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Rich Storytelling Bio Section
                Text(
                    text = "ABOUT ME",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activeMatch.bio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ivory.copy(alpha = 0.85f),
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Compatibility Graph Engine Visualizer
                Text(
                    text = "AI COMPATIBILITY GRAPH",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(12.dp))
                CompatibilityGraph(
                    jsonString = activeMatch.personalityGraphJson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                // Shared Values & Cultural Interconnects
                Text(
                    text = "LIFESTYLE CHOICE",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(0.5.dp, SoftGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = activeMatch.lifestyleChoice, color = Ivory, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .border(0.5.dp, SoftGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = activeMatch.futureAmbition, color = Ivory, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                // AI Generated Introduction segment
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SoftGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MutedNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AURELIA AI INTRODUCTION",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeMatch.aiIntroduction,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ivory.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }

                // Interactive Conversation Starters
                Text(
                    text = "CHOOSE A CONVERSATION PROMPT TO LAUNCH CONVERSATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    activeMatch.conversationStarters.split(",").forEach { starter ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DeepNavy, RoundedCornerShape(2.dp))
                                .clickable {
                                    // Like the match first to unlock the conversation flow
                                    viewModel.likeMatch(activeMatch.id, true)
                                    viewModel.sendMessageTo(activeMatch.id, starter)
                                    viewModel.enterChat(activeMatch.id)
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = starter,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ivory,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Action buttons: Dismiss / Like
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            // Cycle matches by ignoring for today (moves to back or skips)
                            viewModel.likeMatch(activeMatch.id, false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SteelBlue.copy(alpha = 0.5f),
                            contentColor = Ivory
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            text = "PASS",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.likeMatch(activeMatch.id, true)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftGold,
                            contentColor = DeepNavy
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(start = 8.dp)
                            .height(50.dp)
                            .testTag("like_button"),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like icon",
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CONNECTION INDEX",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Elegant placeholder for daily exhaust
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Charcoal, RoundedCornerShape(4.dp))
                    .border(0.5.dp, SoftGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "exhausted",
                        tint = SoftGold,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You've Reviewed Your Curated Cohort",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ivory,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aurelia values emotional patience. Your next curated batch of 5-10 adult introductions will coordinate in the morning.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ivory.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Seed resetting
                            onSignOut()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SteelBlue),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("RESET MATCHES (PROTO RESET)")
                    }
                }
            }
        }
    }
}

// Draw elegant custom profile visuals
@Composable
fun GeometricProfileVisual(matchId: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.width / 2f
        val center = Offset(radius, size.height / 2f)

        // Seed colored background rings relative to match name/id
        val primaryBrushAndColors = when (matchId) {
            "julian" -> Pair(SoftGold, DeepNavy)
            "clara" -> Pair(LightGold, SteelBlue)
            "marcus" -> Pair(GoldAccent, Charcoal)
            else -> Pair(SoftGold, SteelBlue)
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primaryBrushAndColors.first.copy(alpha = 0.8f), primaryBrushAndColors.second),
                center = center,
                radius = radius
            ),
            radius = radius
        )

        // Draw elegant mathematical geometry overlay representing their connection signature
        val numLines = when (matchId) {
            "julian" -> 4  // brutalist rects
            "clara" -> 30  // piano wave string arcs
            "marcus" -> 16 // cognitive neuro lines
            else -> 8
        }

        if (matchId == "julian") {
            // Draw luxury brutalist interlocking frames
            for (i in 1..numLines) {
                val scale = 0.2f * i
                drawRect(
                    color = SoftGold.copy(alpha = 0.3f),
                    topLeft = Offset(center.x - radius * scale, center.y - radius * scale),
                    size = Size(radius * scale * 2, radius * scale * 2),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        } else if (matchId == "clara") {
            // Draw flowing harp string waves
            val path = Path()
            for (i in 0..size.width.toInt() step 5) {
                val yVal = center.y + sin(i * 0.05f) * 30f * cos(i * 0.01f)
                if (i == 0) path.moveTo(i.toFloat(), yVal) else path.lineTo(i.toFloat(), yVal)
            }
            drawPath(
                path = path,
                color = SoftGold.copy(alpha = 0.4f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        } else {
            // Cognitive circles
            for (i in 1..8) {
                drawCircle(
                    color = SoftGold.copy(alpha = 0.25f),
                    radius = radius * 0.12f * i,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Draw connecting axes
                drawLine(
                    color = SoftGold.copy(alpha = 0.15f),
                    start = Offset(center.x - radius, center.y),
                    end = Offset(center.x + radius, center.y)
                )
            }
        }
    }
}

// Drawing five dimensions compatibility bars (Personality, Values, Lifestyle, Ambition, Emotional Intelligence)
@Composable
fun CompatibilityGraph(jsonString: String, modifier: Modifier = Modifier) {
    val items = remember(jsonString) {
        try {
            val obj = JSONObject(jsonString)
            listOf(
                "Personality" to obj.optInt("Personality", 80),
                "Values" to obj.optInt("Values", 85),
                "Lifestyle" to obj.optInt("Lifestyle", 82),
                "Ambition" to obj.optInt("Ambition", 90),
                "Emotional Intell." to obj.optInt("Emotional Intelligence", 88)
            )
        } catch (e: Exception) {
            listOf(
                "Personality" to 85,
                "Values" to 90,
                "Lifestyle" to 80,
                "Ambition" to 92,
                "Emotional Intell." to 89
            )
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (dimension, score) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dimension.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Ivory.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(DeepNavy)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(score / 100f)
                            .fillMaxHeight()
                            .background(SoftGold)
                    )
                }
            }
        }
    }
}

// ==================== CINEMATIC MATCH DIALOG ====================
@Composable
fun CinematicMatchDialog(
    partnerName: String,
    onDismiss: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, SoftGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Charcoal),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "A U R E L I A   B O N D",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "It's a Match.",
                    style = MaterialTheme.typography.displayMedium,
                    color = Ivory,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You and $partnerName have matched with highly aligned compatibility metrics. Your AI introductions are active and writing dialogue parameters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ivory.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onNavigateToChat,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGold, contentColor = DeepNavy),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "ENTER CONVERSATION",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DISMISS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ivory.copy(alpha = 0.5f),
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                )
            }
        }
    }
}

// ==================== CHATS TAB SCREEN ====================
@Composable
fun ChatsTabScreen(
    matches: List<CuratedMatch>,
    viewModel: AureliaViewModel
) {
    val pairedMatches = matches.filter { it.isMatched }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SECURE CONVERSATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = SoftGold,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "Active Dialogue Circles",
            style = MaterialTheme.typography.titleLarge,
            color = Ivory,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pairedMatches.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pairedMatches) { match ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Charcoal, RoundedCornerShape(4.dp))
                            .border(0.5.dp, SoftGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .clickable { viewModel.enterChat(match.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Drawing miniature custom vector avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MutedNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            GeometricProfileVisual(matchId = match.id, modifier = Modifier.fillMaxSize())
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = match.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Ivory,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Active introduction: Tap to view conversation starters...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ivory.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Chat active",
                            tint = SoftGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        } else {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "no conversations",
                        tint = SoftGold.copy(alpha = 0.3f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Cultivate Connections",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ivory
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Once you express a symmetric Connection with daily curated candidates, your secure dialogue circle will be established here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ivory.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

// ==================== EXCLUSIVE CHAT ROOM VIEW ====================
@Composable
fun ChatRoomScreen(
    match: CuratedMatch,
    viewModel: AureliaViewModel,
    onBack: () -> Unit
) {
    val messagesState = viewModel.getMessagesFor(match.id).collectAsState(initial = emptyList())
    var textInput by remember { mutableStateOf("") }

    // Waveform modeling states
    var playVoiceState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Charcoal)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BACK",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MutedNavy),
                    contentAlignment = Alignment.Center
                ) {
                    GeometricProfileVisual(matchId = match.id, modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = match.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ivory,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Compatibility: ${match.compatibilityScore}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGold,
                        fontSize = 9.sp
                    )
                }
            }

            IconButton(onClick = { /* Simulated Voice intro Call */ }) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Simulated Call",
                    tint = SoftGold
                )
            }
        }

        // Shared Interest Prompts
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MutedNavy)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Prompt star",
                        tint = SoftGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prompt: Both of you enjoy ${match.commonInterests.split(",").firstOrNull() ?: "shared legacy"}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ivory.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "ICEBREAKER",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Messaging dialogs
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = false
        ) {
            items(messagesState.value) { message ->
                val isMe = message.sender == "me"
                val isSys = message.sender == "system"

                when {
                    isSys -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftGold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .background(Charcoal, RoundedCornerShape(2.dp))
                                    .border(0.5.dp, SoftGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    else -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) SoftGold else Charcoal
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp,
                                    bottomStart = if (isMe) 4.dp else 0.dp,
                                    bottomEnd = if (isMe) 0.dp else 4.dp
                                ),
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .border(
                                        0.5.dp,
                                        if (isMe) Color.Transparent else SoftGold.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (message.isVoiceNote) {
                                        // Visualizing custom interactive audio note waveform
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { playVoiceState = !playVoiceState },
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(if (isMe) DeepNavy else SoftGold, CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = if (isMe) SoftGold else DeepNavy,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            VoiceWaveformCanvas(
                                                isPlaying = playVoiceState,
                                                fillColor = if (isMe) DeepNavy else SoftGold,
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .height(16.dp)
                                            )
                                            Text(
                                                text = "${message.voiceDurationSec}s",
                                                color = if (isMe) DeepNavy else Ivory,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = message.content,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (isMe) DeepNavy else Ivory,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input bottom segment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Charcoal)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    // Send simulated voice notes
                    viewModel.sendMessageTo(match.id, "[Voice Note]", isVoice = true, duration = 8)
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(MutedNavy, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Voice note icon",
                    tint = SoftGold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Compose deliberate message...", color = Ivory.copy(alpha = 0.4f), fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Ivory,
                    unfocusedTextColor = Ivory,
                    focusedBorderColor = SoftGold,
                    unfocusedBorderColor = SteelBlue,
                    focusedContainerColor = DeepNavy,
                    unfocusedContainerColor = DeepNavy
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessageTo(match.id, textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(SoftGold, RoundedCornerShape(2.dp))
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = DeepNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Simple Interactive waveform canvas drawing
@Composable
fun VoiceWaveformCanvas(isPlaying: Boolean, fillColor: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val numBars = 16
        val barWidth = size.width / (numBars * 1.5f)
        val space = barWidth * 0.5f
        for (i in 0 until numBars) {
            // Wave height modulation
            val heightPercent = if (isPlaying) {
                0.2f + 0.8f * sin((i * 0.6f + System.currentTimeMillis() * 0.01f)).toFloat().coerceAtLeast(0.1f)
            } else {
                listOf(0.3f, 0.6f, 0.8f, 0.5f, 0.2f, 0.6f, 0.7f, 0.9f, 0.4f, 0.3f, 0.5f, 0.8f, 0.4f, 0.6f, 0.3f, 0.1f)[i]
            }
            val h = size.height * heightPercent
            drawRect(
                color = fillColor.copy(alpha = if (isPlaying) 0.9f else 0.5f),
                topLeft = Offset(i * (barWidth + space), (size.height - h) / 2f),
                size = Size(barWidth, h)
            )
        }
    }
}

// ==================== EVENTS TAB SCREEN ====================
@Composable
fun EventsTabScreen(
    events: List<Event>,
    viewModel: AureliaViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "MEMBERS NETWORK EVENTS",
            style = MaterialTheme.typography.labelSmall,
            color = SoftGold,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "The Social Preservation Salon",
            style = MaterialTheme.typography.titleLarge,
            color = Ivory,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Symptomatic offline gatherings designed for deliberate connection. No phone checks, exclusive limits.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ivory.copy(alpha = 0.6f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, SoftGold.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
                    colors = CardDefaults.cardColors(containerColor = Charcoal),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(SteelBlue, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = event.type.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Ivory,
                                    fontSize = 9.sp
                                )
                            }
                            Text(
                                text = "${event.date} | ${event.time}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftGold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = event.location.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Ivory.copy(alpha = 0.4f),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                            fontSize = 11.sp
                        )

                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ivory.copy(alpha = 0.8f),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // RSVP trigger button
                        Button(
                            onClick = {
                                viewModel.toggleEventRSVP(event.id, !event.isJoined)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (event.isJoined) SteelBlue else SoftGold,
                                contentColor = if (event.isJoined) Ivory else DeepNavy
                            ),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (event.isJoined) "✓ ATTENDING (RSVP BOUND)" else "REQUEST SALON RESERVATION",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== COACH TAB SCREEN ====================
@Composable
fun CoachTabScreen(
    viewModel: AureliaViewModel
) {
    val messages by viewModel.coachMessages.collectAsState()
    val isGenerating by viewModel.isCoachGenerating.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Coach Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AURELIA AI INTELLECT",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    letterSpacing = 2.sp,
                )
                Text(
                    text = "AI Relationship Coach",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ivory,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { viewModel.clearCoachHistory() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Clear Coach history",
                    tint = SoftGold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chat flow
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Charcoal, RoundedCornerShape(4.dp))
                .border(0.5.dp, SoftGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                val isModel = message.role == "model"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isModel) MutedNavy else SoftGold
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .border(
                                0.5.dp,
                                if (isModel) SoftGold.copy(alpha = 0.3f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isModel) "AURELIA COACH" else "YOU",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isModel) SoftGold else DeepNavy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isModel) Ivory else DeepNavy,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MutedNavy, RoundedCornerShape(4.dp))
                                .border(0.5.dp, SoftGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = SoftGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "AI Coach is typing...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Ivory.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input coach segment
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Consult coach about profile, bios...", color = Ivory.copy(alpha = 0.4f), fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("coach_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Ivory,
                    unfocusedTextColor = Ivory,
                    focusedBorderColor = SoftGold,
                    unfocusedBorderColor = SteelBlue,
                    focusedContainerColor = Charcoal,
                    unfocusedContainerColor = Charcoal
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendCoachMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(SoftGold, RoundedCornerShape(2.dp))
                    .testTag("coach_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = DeepNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==================== INSIGHTS TAB SCREEN ====================
@Composable
fun InsightsTabScreen(
    userProfile: UserProfile?,
    viewModel: AureliaViewModel,
    onSignOut: () -> Unit
) {
    var identityVerified by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "COGNITIVE INDEX & INSIGHTS",
            style = MaterialTheme.typography.labelSmall,
            color = SoftGold,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "Personal Alignment Portal",
            style = MaterialTheme.typography.titleLarge,
            color = Ivory,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trust score representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, SoftGold.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = Charcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR TRUST SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "EXCEPTIONAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${userProfile?.trustScore ?: 94}%",
                        style = MaterialTheme.typography.displayLarge,
                        color = Ivory,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Identity Verification Check: ✓ SECURE",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ivory.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Profile Authenticity checks: Approved",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ivory.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { (userProfile?.trustScore ?: 94) / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = SoftGold,
                    trackColor = DeepNavy,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Membership Tiers Dashboard info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, SoftGold.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = Charcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AURELIA RESIDENCY",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${userProfile?.name?.uppercase()}'S MEMBER SECTOR: ${userProfile?.membershipTier?.uppercase() ?: "ESSENTIAL"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ivory,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val benefits = when (userProfile?.membershipTier) {
                    "Premium" -> "Unlimited introductions, advanced matching analytics access, direct relationship coach module."
                    "Concierge" -> "Custom human matches, private private lounge events, luxury table salons, VIP concierge support."
                    else -> "Core matches (5-10 introductions per day), secure conversations, and standard salon RSVP access."
                }

                Text(
                    text = benefits,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ivory.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual charts for personality trends (Aesthetics, Intellect, etc.)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, SoftGold.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = Charcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "YOUR RELATIONSHIP INSIGHTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Symmetric Living", style = MaterialTheme.typography.labelSmall, color = Ivory.copy(alpha = 0.5f))
                        Text("91%", style = MaterialTheme.typography.titleLarge, color = Ivory, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Conversation Pacing", style = MaterialTheme.typography.labelSmall, color = Ivory.copy(alpha = 0.5f))
                        Text("88%", style = MaterialTheme.typography.titleLarge, color = Ivory, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You align strongly with members who seek creative depth, value slow messaging protocols, and prioritize long-term residential structure over brief proximity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ivory.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign out button
        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = SteelBlue.copy(alpha = 0.5f), contentColor = Ivory),
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("sign_out_button")
        ) {
            Text(
                text = "RESET AURELIA RESIDENCY",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp
            )
        }
    }
}
