package com.statusking.ai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.statusking.ai.data.local.PreferencesManager
import com.statusking.ai.model.StatusItem
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.repository.AuthRepository
import com.statusking.ai.repository.StatusRepository
import com.statusking.ai.repository.UserProfile
import com.statusking.ai.ui.components.AdBannerView
import com.statusking.ai.ui.components.StatusCard
import com.statusking.ai.ui.components.StatusTopBar
import com.statusking.ai.ui.theme.AccentGold
import com.statusking.ai.ui.theme.AccentNeonPink
import com.statusking.ai.ui.theme.PrimaryViolet
import com.statusking.ai.ui.theme.PrimaryVioletLight
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    isPremium: Boolean,
    themeMode: String,
    favorites: List<StatusItem>,
    creations: List<StatusItem>,
    statusRepository: StatusRepository,
    authRepository: AuthRepository,
    preferencesManager: PreferencesManager,
    onNavigateToPremium: () -> Unit,
    onUseInEditor: (StatusItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0: Favorites, 1: Creations, 2: Settings
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.displayName) }
    var editAvatar by remember { mutableStateOf(userProfile.avatarEmoji) }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf(false) }

    val avatars = listOf("👑", "🦁", "🐯", "🚀", "💎", "🔥", "✨", "🦅")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen")
    ) {
        StatusTopBar(
            isPremium = isPremium,
            onPremiumClick = onNavigateToPremium
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Ad banner
            item {
                AdBannerView(isPremium = isPremium)
            }

            // Guest Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_user_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Emoji Circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryViolet, PrimaryVioletLight, AccentGold)
                                    )
                                )
                                .clickable { showEditProfileDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = userProfile.avatarEmoji, fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isPremium) AccentGold else MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = if (isPremium) "VIP PRO" else "Guest Creator",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPremium) Color(0xFF382300) else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "${favorites.size} favs • ${creations.size} posters",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Profile Navigation Tabs: Favorites, Creations, Settings
            item {
                Spacer(modifier = Modifier.height(18.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Favorites (${favorites.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Creations (${creations.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            // Tab 0: Saved Favorites
            if (selectedTab == 0) {
                if (favorites.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "❤️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No favorites saved yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the heart on any status to save it here.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                } else {
                    items(favorites, key = { it.id }) { item ->
                        StatusCard(
                            status = item,
                            onFavoriteToggle = {
                                coroutineScope.launch { statusRepository.toggleFavorite(it) }
                            },
                            onUseInEditor = onUseInEditor,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            // Tab 1: My Creations
            if (selectedTab == 1) {
                if (creations.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎨", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No creations yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Customize and tap 'Save' in the editor to store your posters here.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                } else {
                    items(creations, key = { it.id }) { creation ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = creation.text,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        maxLines = 3
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Saved creation • ${creation.tone}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = PrimaryVioletLight)
                                    )
                                }

                                Row {
                                    IconButton(onClick = { onUseInEditor(creation) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit in Canvas", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        coroutineScope.launch { statusRepository.deleteById(creation.id) }
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Settings Section
            if (selectedTab == 2) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 1. Theme Mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = PrimaryVioletLight)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Theme Mode", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Current: $themeMode", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("SYSTEM", "DARK", "LIGHT").forEach { mode ->
                                    FilterChip(
                                        selected = themeMode.equals(mode, ignoreCase = true),
                                        onClick = { preferencesManager.setThemeMode(mode) },
                                        label = { Text(mode) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. Default Language
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Language, contentDescription = null, tint = PrimaryVioletLight)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Default Language", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Select primary status language", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(StatusLanguage.entries) { lang ->
                                    FilterChip(
                                        selected = preferencesManager.appLanguage.value == lang,
                                        onClick = { preferencesManager.setAppLanguage(lang) },
                                        label = { Text(lang.displayName) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. Clear Cache
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        context.cacheDir.deleteRecursively()
                                        Toast.makeText(context, "Temporary cache cleared!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CleaningServices, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Clear Temporary Cache", style = MaterialTheme.typography.bodyMedium)
                                }
                                Text("Clean", color = PrimaryVioletLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            // 4. Privacy Policy & Terms of Service
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPolicyDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Policy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Privacy Policy & Terms of Service", style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            // 5. About App
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAboutDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("About StatusKing AI", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit Creator Profile", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Display Name", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Pick Avatar Emoji", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            avatars.forEach { av ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (editAvatar == av) PrimaryVioletLight else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.clickable { editAvatar = av }
                                ) {
                                    Text(text = av, fontSize = 24.sp, modifier = Modifier.padding(6.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                authRepository.updateProfile(editName.ifBlank { "Creator" }, editAvatar)
                                showEditProfileDialog = false
                                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Privacy Policy Dialog
        if (showPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPolicyDialog = false },
                title = { Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "StatusKing AI respects your privacy. All your status generation, saved creations, and personal preferences stay on your device unless you choose to share them externally.\n\n" +
                                "We do not sell personal data. Photos selected for poster backgrounds are processed locally on your device.\n\n" +
                                "Terms: Generated statuses and posters are for personal and social media sharing. Have fun and create awesome content!",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                confirmButton = {
                    Button(onClick = { showPolicyDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // About App Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("About StatusKing AI", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("StatusKing AI v1.0.0", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Indian social-media status, quote, Shayari, meme and poster creation app.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Built with Kotlin & Jetpack Compose")
                        Text("• Offline-first template engine")
                        Text("• 9:16 high-resolution export for WhatsApp & Instagram")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Proudly made for creators in India 🇮🇳")
                    }
                },
                confirmButton = {
                    Button(onClick = { showAboutDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
