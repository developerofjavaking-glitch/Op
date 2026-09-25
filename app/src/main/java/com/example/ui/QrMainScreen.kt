package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.ClassicBg
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicBlueContainer
import com.example.ui.theme.ClassicBorder
import com.example.ui.theme.ClassicSurface
import com.example.ui.theme.TextDarkMuted

enum class QrNavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    SCAN("Scan", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner, "tab_scan"),
    CREATE("Create", Icons.Filled.QrCode, Icons.Outlined.QrCode, "tab_create"),
    HISTORY("Saved", Icons.Filled.History, Icons.Outlined.History, "tab_history")
}

@Composable
fun QrMainScreen(viewModel: QrViewModel) {
    var selectedTab by remember { mutableStateOf(QrNavigationTab.SCAN) }

    val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()
    val activeResult by viewModel.activeResult.collectAsStateWithLifecycle()
    val isCurrentFavorite by viewModel.isCurrentFavorite.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ClassicBg),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(ClassicSurface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                HorizontalDivider(color = ClassicBorder, thickness = 1.dp)
                NavigationBar(
                    modifier = Modifier.testTag("qr_bottom_navigation"),
                    containerColor = ClassicSurface,
                    tonalElevation = 0.dp
                ) {
                    QrNavigationTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ClassicBlue,
                                selectedTextColor = ClassicBlue,
                                unselectedIconColor = TextDarkMuted,
                                unselectedTextColor = TextDarkMuted,
                                indicatorColor = ClassicBlueContainer
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Crossfade(
                targetState = selectedTab,
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    QrNavigationTab.SCAN -> ScannerScreen(
                        currentResult = activeResult,
                        isCurrentFavorite = isCurrentFavorite,
                        onQrDetected = { raw -> viewModel.onQrScanned(raw) },
                        onToggleFavorite = { viewModel.toggleCurrentResultFavorite() },
                        onDismissResult = { viewModel.dismissResult() }
                    )
                    QrNavigationTab.CREATE -> GeneratorScreen(
                        onSaveGenerated = { raw, title, type ->
                            viewModel.saveGeneratedQr(raw, title, type)
                        }
                    )
                    QrNavigationTab.HISTORY -> HistoryScreen(
                        records = historyRecords,
                        onSelectRecord = { record ->
                            viewModel.selectRecordFromHistory(record)
                            selectedTab = QrNavigationTab.SCAN
                        },
                        onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                        onDeleteRecord = { id -> viewModel.deleteRecord(id) },
                        onClearAll = { viewModel.clearAllHistory() }
                    )
                }
            }
        }
    }
}
