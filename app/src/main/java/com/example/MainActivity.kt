package com.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.UserProfile
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AureliaViewModel
import com.example.ui.viewmodel.AureliaViewModelFactory

enum class AppState {
    Landing,
    Onboarding,
    Dashboard
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Standard ViewModel Provider initialization (fully robust)
        val viewModel = ViewModelProvider(
            this,
            AureliaViewModelFactory(application)
        )[AureliaViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val userProfile by viewModel.userProfile.collectAsState()
                var currentAppState by remember { mutableStateOf(AppState.Landing) }

                // Automatically forward to dashboard if profile is detected in Room db
                LaunchedEffect(userProfile) {
                    if (userProfile != null) {
                        currentAppState = AppState.Dashboard
                    } else if (currentAppState == AppState.Dashboard) {
                        // If profile was deleted (Residency reset), return to landingscreen
                        currentAppState = AppState.Landing
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentAppState) {
                            AppState.Landing -> {
                                LandingScreen(
                                    onJoinClick = {
                                        currentAppState = AppState.Onboarding
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            AppState.Onboarding -> {
                                OnboardingScreen(
                                    onComplete = { profile ->
                                        // Persist user profile to database
                                        viewModel.saveProfile(profile)
                                        currentAppState = AppState.Dashboard
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            AppState.Dashboard -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onSignOut = {
                                        // Clear profile and resets database triggers
                                        currentAppState = AppState.Landing
                                        // Reset default seed manually by cleaning the profile segment in DB
                                        // We will trigger a temporary signout handle
                                        com.example.data.AureliaDatabase.getInstance(applicationContext).let { rdb ->
                                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                                rdb.clearAllTables()
                                                // Seed fresh database content upon clearing so next boot is clean
                                                com.example.data.AureliaDatabase.seedData(applicationContext)
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
