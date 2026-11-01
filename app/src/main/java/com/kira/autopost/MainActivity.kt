package com.kira.autopost

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.kira.autopost.service.AutoPostForegroundService
import com.kira.autopost.ui.components.AppHeader
import com.kira.autopost.ui.components.AppTab
import com.kira.autopost.ui.components.BottomNavBar
import com.kira.autopost.ui.screens.AccountsScreen
import com.kira.autopost.ui.screens.GroupsScreen
import com.kira.autopost.ui.screens.HomeScreen
import com.kira.autopost.ui.screens.PostEditorScreen
import com.kira.autopost.ui.screens.ReportsScreen
import com.kira.autopost.ui.screens.SettingsScreen
import com.kira.autopost.ui.theme.KiraAutoPostTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            KiraAutoPostTheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        val app = KiraApplication.instance
        var currentTab by remember { mutableStateOf(AppTab.HOME) }
        val isPostingActive by app.appPreferences.isPostingActive.collectAsState()

        val togglePosting = {
            if (isPostingActive) {
                AutoPostForegroundService.stopService(this)
            } else {
                AutoPostForegroundService.startService(this)
            }
        }

        Scaffold(
            topBar = {
                AppHeader(
                    onSettingsClick = { currentTab = AppTab.SETTINGS }
                )
            },
            bottomBar = {
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        ) { innerPadding ->
            val modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            when (currentTab) {
                AppTab.HOME -> HomeScreen(
                    appPreferences = app.appPreferences,
                    onNavigateTab = { currentTab = it },
                    onTogglePosting = togglePosting,
                    modifier = modifier
                )
                AppTab.ACCOUNTS -> AccountsScreen(
                    appPreferences = app.appPreferences,
                    tdLibManager = app.tdLibManager,
                    modifier = modifier
                )
                AppTab.GROUPS -> GroupsScreen(
                    appPreferences = app.appPreferences,
                    tdLibManager = app.tdLibManager,
                    modifier = modifier
                )
                AppTab.POST -> PostEditorScreen(
                    appPreferences = app.appPreferences,
                    modifier = modifier
                )
                AppTab.REPORTS -> ReportsScreen(
                    appPreferences = app.appPreferences,
                    modifier = modifier
                )
                AppTab.SETTINGS -> SettingsScreen(
                    appPreferences = app.appPreferences,
                    modifier = modifier
                )
            }
        }
    }
}
