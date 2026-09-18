package com.statusking.ai.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.statusking.ai.model.StatusCategory
import com.statusking.ai.model.StatusItem
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.model.StatusTone
import com.statusking.ai.repository.AiRepository
import com.statusking.ai.ui.components.AdBannerView
import com.statusking.ai.ui.components.StatusCard
import com.statusking.ai.ui.components.StatusTopBar
import com.statusking.ai.ui.theme.AccentGold
import com.statusking.ai.ui.theme.AccentNeonPink
import com.statusking.ai.ui.theme.PrimaryViolet
import com.statusking.ai.ui.theme.PrimaryVioletDark
import com.statusking.ai.ui.theme.PrimaryVioletLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    isPremium: Boolean,
    aiRepository: AiRepository,
    onNavigateToPremium: () -> Unit,
    onNavigateToCreateWithStatus: (StatusItem) -> Unit,
    onFavoriteToggle: (StatusItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var topicInput by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(StatusLanguage.HINGLISH) }
    var selectedTone by remember { mutableStateOf(StatusTone.ATTITUDE) }
    var selectedCategory by remember { mutableStateOf(StatusCategory.ATTITUDE) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedStatuses by remember { mutableStateOf<List<StatusItem>>(emptyList()) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val trendingCategories = listOf(
        StatusCategory.ATTITUDE,
        StatusCategory.LOVE,
        StatusCategory.FUNNY,
        StatusCategory.SHAYARI,
        StatusCategory.FESTIVAL,
        StatusCategory.GOOD_MORNING,
        StatusCategory.GOOD_NIGHT,
        StatusCategory.MOTIVATION
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Top App Bar
        StatusTopBar(
            isPremium = isPremium,
            onPremiumClick = onNavigateToPremium
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Ad Banner for Free Users
            item {
                AdBannerView(isPremium = isPremium)
            }

            // Hero Card: "AI Status Generator"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("hero_generator_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        PrimaryVioletDark,
                                        PrimaryViolet,
                                        Color(0xFF3700B3)
                                    )
                                )
                            )
                    ) {
                        // Background hero graphic
                        AsyncImage(
                            model = R.drawable.hero_banner,
                            contentDescription = "Hero Graphic",
                            contentScale = ContentScale.Crop,
                            alpha = 0.25f,
                            modifier = Modifier.matchParentSize()
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(AccentGold.copy(alpha = 0.25f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "STATUSKING ENGINE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentGold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "AI Status Generator",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )

                            Text(
                                text = "Create viral status in seconds",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Topic Input Field
                            OutlinedTextField(
                                value = topicInput,
                                onValueChange = { topicInput = it },
                                placeholder = {
                                    Text(
                                        text = "What's on your mind? (e.g. Bhai ki attitude, chai, exam...)",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Black.copy(alpha = 0.35f),
                                    unfocusedContainerColor = Color.Black.copy(alpha = 0.25f),
                                    focusedBorderColor = AccentGold,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("topic_input_field")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Language Selector (Hindi, Hinglish, English, Bengali)
                            Text(
                                text = "Choose Language",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusLanguage.entries.forEach { lang ->
                                    val isSelected = selectedLanguage == lang
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedLanguage = lang },
                                        label = {
                                            Text(
                                                text = "${lang.displayName} (${lang.nativeName})",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AccentGold,
                                            selectedLabelColor = Color(0xFF2E1C00),
                                            containerColor = Color.Black.copy(alpha = 0.25f),
                                            labelColor = Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier.testTag("language_chip_${lang.name}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Tone Selector (Attitude, Love, Funny, Sad, Motivational, Romantic, Friendship, Festival, Savage)
                            Text(
                                text = "Select Tone",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusTone.entries.forEach { tone ->
                                    val isSelected = selectedTone == tone
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedTone = tone },
                                        label = {
                                            Text(
                                                text = "${tone.emoji} ${tone.displayName}",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryVioletLight,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.Black.copy(alpha = 0.25f),
                                            labelColor = Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) PrimaryVioletLight else Color.White.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier.testTag("tone_chip_${tone.name}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Main Action Button: "Generate 5 Status"
                            Button(
                                onClick = {
                                    val queryTopic = topicInput.ifEmpty { selectedTone.displayName }
                                    isGenerating = true
                                    statusMessage = "Creating your status..."
                                    coroutineScope.launch {
                                        val result = aiRepository.generateStatuses(
                                            topic = queryTopic,
                                            language = selectedLanguage,
                                            tone = selectedTone,
                                            category = selectedCategory
                                        )
                                        isGenerating = false
                                        result.onSuccess { list ->
                                            generatedStatuses = list
                                            statusMessage = "5 fresh status ideas created!"
                                            Toast.makeText(context, "5 fresh status ideas created!", Toast.LENGTH_SHORT).show()
                                        }.onFailure {
                                            statusMessage = "Failed to generate status. Please try again."
                                        }
                                    }
                                },
                                enabled = !isGenerating,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentGold,
                                    contentColor = Color(0xFF2E1C00)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("generate_status_main_button")
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color(0xFF2E1C00),
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Creating your status...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "✨ Generate 5 Status",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Status message toast / banner
            item {
                AnimatedVisibility(
                    visible = statusMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    statusMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Generated Status Results Section (if available)
            if (generatedStatuses.isNotEmpty()) {
                item {
                    Text(
                        text = "Generated AI Statuses",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(start = 18.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(generatedStatuses) { status ->
                    StatusCard(
                        status = status,
                        onFavoriteToggle = onFavoriteToggle,
                        onUseInEditor = onNavigateToCreateWithStatus,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Trending Categories Section
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = AccentNeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trending Categories",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trendingCategories.forEach { category ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable {
                                    selectedCategory = category
                                    topicInput = category.displayName
                                    Toast.makeText(context, "Selected: ${category.displayName}", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("trending_cat_${category.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = category.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Premium Promotional Card: "Go Premium • No ads • No watermark • HD export"
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("premium_promo_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPremium) AccentGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.5.dp, AccentGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isPremium) "VIP Member" else "Go Premium",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = AccentGold
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No ads • No watermark • HD export • VIP fonts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Button(
                            onClick = onNavigateToPremium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGold,
                                contentColor = Color(0xFF2E1C00)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("unlock_premium_button")
                        ) {
                            Text(
                                text = if (isPremium) "VIP Pass" else "Unlock",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
