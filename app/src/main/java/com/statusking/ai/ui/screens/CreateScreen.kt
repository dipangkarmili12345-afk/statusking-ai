package com.statusking.ai.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.statusking.ai.data.local.SampleData
import com.statusking.ai.model.FontStyleType
import com.statusking.ai.model.PosterConfig
import com.statusking.ai.model.StatusCategory
import com.statusking.ai.model.StatusItem
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.model.StatusTone
import com.statusking.ai.repository.AiRepository
import com.statusking.ai.repository.StatusRepository
import com.statusking.ai.service.ExportManager
import com.statusking.ai.service.ExportResult
import com.statusking.ai.ui.components.CustomPosterCanvas
import com.statusking.ai.ui.components.StatusTopBar
import com.statusking.ai.ui.theme.AccentGold
import com.statusking.ai.ui.theme.AccentNeonPink
import com.statusking.ai.ui.theme.PrimaryViolet
import com.statusking.ai.ui.theme.PrimaryVioletDark
import com.statusking.ai.ui.theme.PrimaryVioletLight
import kotlinx.coroutines.launch

@Composable
fun CreateScreen(
    initialStatus: StatusItem?,
    isPremium: Boolean,
    aiRepository: AiRepository,
    statusRepository: StatusRepository,
    exportManager: ExportManager,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var config by remember {
        mutableStateOf(
            PosterConfig(
                text = initialStatus?.text ?: "Apna time aayega nahi, apna time hum layenge! 👑",
                watermarkEnabled = !isPremium
            )
        )
    }

    // Undo / Redo history stacks
    val undoStack = remember { mutableStateListOf<PosterConfig>() }
    val redoStack = remember { mutableStateListOf<PosterConfig>() }

    fun updateConfigWithUndo(newConfig: PosterConfig) {
        undoStack.add(config)
        redoStack.clear()
        config = newConfig
    }

    fun handleUndo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(config)
            config = prev
        }
    }

    fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(config)
            config = next
        }
    }

    // Update if initialStatus changes
    LaunchedEffect(initialStatus) {
        if (initialStatus != null && initialStatus.text != config.text) {
            updateConfigWithUndo(config.copy(text = initialStatus.text))
        }
    }

    // AI Idea generation state
    var aiTopicInput by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(StatusLanguage.HINGLISH) }
    var selectedTone by remember { mutableStateOf(StatusTone.ATTITUDE) }
    var isGeneratingIdeas by remember { mutableStateOf(false) }
    var aiIdeas by remember { mutableStateOf<List<StatusItem>>(emptyList()) }

    // Tab state for editor tools (0: Text & Font, 1: Background & Photo, 2: Templates & Emoji)
    var selectedTab by remember { mutableStateOf(0) }

    // Export states
    var isExporting by remember { mutableStateOf(false) }

    // Photo picker launcher (Android Photo Picker - compliant with Google Play Policy)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            updateConfigWithUndo(config.copy(imageUri = uri.toString()))
            Toast.makeText(context, "Photo added to poster!", Toast.LENGTH_SHORT).show()
        }
    }

    val availableColors = listOf(
        0xFFFFFFFF, 0xFFFFD700, 0xFFFF3366, 0xFF00FFFF,
        0xFF00FF7F, 0xFFFF8C00, 0xFFE0E0E0, 0xFF1A1A1A
    )

    val emojis = listOf("✨", "👑", "🔥", "❤️", "⚡", "🌹", "🦁", "☕", "🪔", "🤝", "🚀", "🍕")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_screen")
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
            // Section 1: AI Idea Generator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = PrimaryVioletLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generate 5 AI Status Ideas",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = aiTopicInput,
                            onValueChange = { aiTopicInput = it },
                            placeholder = { Text("Topic: e.g. Dosti, Gym, Samosa, Attitude...", fontSize = 14.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_ai_topic_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusLanguage.entries.forEach { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(lang.displayName) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                isGeneratingIdeas = true
                                coroutineScope.launch {
                                    val result = aiRepository.generateStatuses(
                                        topic = aiTopicInput.ifEmpty { "Viral Status" },
                                        language = selectedLanguage,
                                        tone = selectedTone,
                                        category = StatusCategory.VIRAL
                                    )
                                    isGeneratingIdeas = false
                                    result.onSuccess { ideas ->
                                        aiIdeas = ideas
                                    }
                                }
                            },
                            enabled = !isGeneratingIdeas,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_generate_ideas_button")
                        ) {
                            if (isGeneratingIdeas) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Ideas...")
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("✨ Generate 5 Status Ideas", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Generated Ideas Carousel / Chips
                        if (aiIdeas.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tap any idea to load into editor:",
                                style = MaterialTheme.typography.labelSmall.copy(color = PrimaryVioletLight)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(aiIdeas) { idea ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, PrimaryViolet.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .width(220.dp)
                                            .clickable {
                                                updateConfigWithUndo(config.copy(text = idea.text))
                                                Toast.makeText(context, "Status loaded!", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Text(
                                            text = idea.text,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            maxLines = 3,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: 9:16 Social Media Poster Preview Canvas with Drag Support
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "9:16 Status Preview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Row {
                        // Undo Button
                        IconButton(
                            onClick = { handleUndo() },
                            enabled = undoStack.isNotEmpty(),
                            modifier = Modifier.testTag("undo_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = if (undoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }

                        // Redo Button
                        IconButton(
                            onClick = { handleRedo() },
                            enabled = redoStack.isNotEmpty(),
                            modifier = Modifier.testTag("redo_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo",
                                tint = if (redoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }

                        // Reset Button
                        IconButton(
                            onClick = {
                                updateConfigWithUndo(
                                    PosterConfig(
                                        text = "Apna time aayega nahi, apna time hum layenge! 👑",
                                        watermarkEnabled = !isPremium
                                    )
                                )
                            },
                            modifier = Modifier.testTag("reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = "Reset editor",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                // The interactive 9:16 Poster Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomPosterCanvas(
                        config = config,
                        isPremium = isPremium,
                        onDragDelta = { dx, dy ->
                            config = config.copy(
                                textOffsetX = config.textOffsetX + dx,
                                textOffsetY = config.textOffsetY + dy
                            )
                        }
                    )
                }
            }

            // Quick Drag Tip
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Tip: Drag status text to move anywhere on the poster",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            // Section 3: Editor Controls Toolset Tabs
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
                        text = { Text("Text & Font", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.TextFields, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Background", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Templates", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // Tab 0: Text & Font Controls
            if (selectedTab == 0) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Text Input Field
                            Text("Edit Status Text", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = config.text,
                                onValueChange = { updateConfigWithUndo(config.copy(text = it)) },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("editor_text_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Font Family Selector
                            Text("Select Font Family", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FontStyleType.entries.forEachIndexed { index, fontType ->
                                    FilterChip(
                                        selected = config.fontIndex == index,
                                        onClick = { updateConfigWithUndo(config.copy(fontIndex = index)) },
                                        label = { Text(fontType.label) },
                                        modifier = Modifier.testTag("font_chip_$index")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Font Size Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Font Size", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${config.fontSizeSp.toInt()} sp", style = MaterialTheme.typography.labelMedium)
                            }
                            Slider(
                                value = config.fontSizeSp,
                                onValueChange = { config = config.copy(fontSizeSp = it) },
                                onValueChangeFinished = { updateConfigWithUndo(config) },
                                valueRange = 16f..40f,
                                colors = SliderDefaults.colors(thumbColor = PrimaryViolet, activeTrackColor = PrimaryVioletLight),
                                modifier = Modifier.testTag("font_size_slider")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Styling Toggles: Bold, Italic, Alignment (Left, Center, Right)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Bold
                                    FilterChip(
                                        selected = config.isBold,
                                        onClick = { updateConfigWithUndo(config.copy(isBold = !config.isBold)) },
                                        label = { Icon(Icons.Filled.FormatBold, contentDescription = "Bold", modifier = Modifier.size(18.dp)) }
                                    )
                                    // Italic
                                    FilterChip(
                                        selected = config.isItalic,
                                        onClick = { updateConfigWithUndo(config.copy(isItalic = !config.isItalic)) },
                                        label = { Icon(Icons.Filled.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(18.dp)) }
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { updateConfigWithUndo(config.copy(textAlign = 0)) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.FormatAlignLeft,
                                            contentDescription = "Align Left",
                                            tint = if (config.textAlign == 0) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { updateConfigWithUndo(config.copy(textAlign = 1)) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.FormatAlignCenter,
                                            contentDescription = "Align Center",
                                            tint = if (config.textAlign == 1) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { updateConfigWithUndo(config.copy(textAlign = 2)) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.FormatAlignRight,
                                            contentDescription = "Align Right",
                                            tint = if (config.textAlign == 2) PrimaryVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Text Color Palette
                            Text("Text Color", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                availableColors.forEach { colorHex ->
                                    val isSelected = config.textColor == colorHex
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorHex))
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) AccentGold else Color.Gray.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            )
                                            .clickable { updateConfigWithUndo(config.copy(textColor = colorHex)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Background, Photo & Watermark
            if (selectedTab == 1) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Add Photo from Gallery Button
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_photo_button")
                            ) {
                                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (config.imageUri != null) "Change Background Photo" else "Add Photo from Gallery",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (config.imageUri != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Dim / Blur Background Image", style = MaterialTheme.typography.bodySmall)
                                    Switch(
                                        checked = config.blurBackground,
                                        onCheckedChange = { updateConfigWithUndo(config.copy(blurBackground = it)) }
                                    )
                                }
                                Button(
                                    onClick = { updateConfigWithUndo(config.copy(imageUri = null)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Remove Custom Photo (Use Gradients)", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Gradient Background Selector
                            Text("Gradient Presets (Indian Colorways)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                itemsIndexed(SampleData.GRADIENTS) { index, gradientList ->
                                    val isSelected = config.gradientIndex == index && config.imageUri == null
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Brush.linearGradient(gradientList.map { Color(it) }))
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) AccentGold else Color.White.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                updateConfigWithUndo(config.copy(gradientIndex = index, imageUri = null))
                                            }
                                            .testTag("gradient_preset_$index")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(14.dp))

                            // Watermark Setting
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "StatusKing Watermark",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (!isPremium) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Filled.Lock,
                                                contentDescription = "VIP only",
                                                tint = AccentGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isPremium) "VIP unlocked: Toggle watermark freely" else "Upgrade to VIP to remove watermark",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }

                                Switch(
                                    checked = config.watermarkEnabled,
                                    onCheckedChange = { checked ->
                                        if (!isPremium && !checked) {
                                            onNavigateToPremium()
                                        } else {
                                            updateConfigWithUndo(config.copy(watermarkEnabled = checked))
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PrimaryViolet,
                                        checkedTrackColor = PrimaryVioletLight
                                    ),
                                    modifier = Modifier.testTag("watermark_toggle")
                                )
                            }
                        }
                    }
                }
            }

            // Tab 2: Starter Templates & Emoji Stickers
            if (selectedTab == 2) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Emoji / Sticker Selector
                            Text("Decorative Sticker / Emoji", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                emojis.forEach { emoji ->
                                    val isSelected = config.emoji == emoji
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) PrimaryVioletLight else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.clickable {
                                            updateConfigWithUndo(config.copy(emoji = if (isSelected) null else emoji))
                                        }
                                    ) {
                                        Text(text = emoji, fontSize = 22.sp, modifier = Modifier.padding(8.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Local Starter Templates
                            Text("Starter Indian Poster Templates", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(SampleData.STARTER_TEMPLATES) { template ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, PrimaryViolet.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .width(180.dp)
                                            .clickable {
                                                if (template.isPremium && !isPremium) {
                                                    onNavigateToPremium()
                                                } else {
                                                    val matchedGradientIndex = SampleData.GRADIENTS.indexOfFirst {
                                                        it == template.gradientColors
                                                    }.takeIf { it >= 0 } ?: 0

                                                    updateConfigWithUndo(
                                                        config.copy(
                                                            text = template.quote,
                                                            gradientIndex = matchedGradientIndex,
                                                            textColor = template.textColorHex,
                                                            emoji = template.decorativeEmoji,
                                                            fontIndex = template.fontType.ordinal,
                                                            imageUri = null
                                                        )
                                                    )
                                                    Toast.makeText(context, "Applied: ${template.title}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = template.title,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                                )
                                                if (template.isPremium) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Lock,
                                                        contentDescription = "VIP",
                                                        tint = AccentGold,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = template.quote,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Export Buttons: "Save", "Share", "Download"
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save Button (Saves to Room Database creations)
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                statusRepository.saveCreation(
                                    text = config.text,
                                    category = "Creation",
                                    tone = selectedTone.displayName,
                                    language = selectedLanguage.displayName,
                                    imagePath = config.imageUri
                                )
                                Toast.makeText(context, "Status saved to My Creations! ✨", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_button")
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }

                    // Share Button (Direct Android Sharesheet)
                    Button(
                        onClick = {
                            isExporting = true
                            coroutineScope.launch {
                                val bitmap = exportManager.exportPosterBitmap(config, isPremium)
                                val shared = exportManager.shareStatus(bitmap, config.text)
                                isExporting = false
                                if (shared) {
                                    Toast.makeText(context, "Ready to share!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isExporting,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("share_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontWeight = FontWeight.Bold)
                    }

                    // Download Button (Save High-Res PNG to Gallery)
                    Button(
                        onClick = {
                            isExporting = true
                            coroutineScope.launch {
                                val bitmap = exportManager.exportPosterBitmap(config, isPremium)
                                val result = exportManager.saveToGallery(bitmap)
                                isExporting = false
                                when (result) {
                                    is ExportResult.Success -> {
                                        Toast.makeText(context, "Status saved successfully to Pictures/StatusKingAI! 💾", Toast.LENGTH_LONG).show()
                                    }
                                    is ExportResult.Error -> {
                                        Toast.makeText(context, "Export error: ${result.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = !isExporting,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold,
                            contentColor = Color(0xFF2E1C00)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("download_button")
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF2E1C00), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
