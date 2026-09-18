package com.statusking.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.statusking.ai.data.local.SampleData
import com.statusking.ai.model.FontStyleType
import com.statusking.ai.model.PosterConfig
import kotlin.math.roundToInt

@Composable
fun CustomPosterCanvas(
    config: PosterConfig,
    isPremium: Boolean,
    onDragDelta: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = SampleData.GRADIENTS.getOrElse(config.gradientIndex) { SampleData.GRADIENTS[0] }
        .map { Color(it) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .testTag("custom_poster_canvas"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Background Layer (Custom Image or Linear Gradient)
        if (!config.imageUri.isNullOrEmpty()) {
            AsyncImage(
                model = config.imageUri,
                contentDescription = "Poster Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Darken / Tint overlay for text contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (config.blurBackground) 0.65f else 0.45f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(gradientColors))
            )
            // Subtle ambient particle/circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                            radius = 600f
                        )
                    )
            )
        }

        // 2. Decorative Emoji / Sticker
        if (!config.emoji.isNullOrEmpty()) {
            Text(
                text = config.emoji,
                fontSize = 44.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            )
        }

        // 3. Draggable / Resizable Overlay Text
        val textAlign = when (config.textAlign) {
            0 -> TextAlign.Start
            2 -> TextAlign.End
            else -> TextAlign.Center
        }

        val fontFamily = when (config.fontIndex) {
            1 -> FontStyleType.SERIF.fontFamily
            2 -> FontStyleType.MONOSPACE.fontFamily
            3 -> FontStyleType.CURSIVE.fontFamily
            else -> FontStyleType.SANS_SERIF.fontFamily
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(config.textOffsetX.roundToInt(), config.textOffsetY.roundToInt()) }
                .rotate(config.rotationAngle)
                .fillMaxWidth(0.85f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDragDelta(dragAmount.x, dragAmount.y)
                    }
                }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = config.text.ifEmpty { "Enter your status text..." },
                color = Color(config.textColor),
                fontSize = config.fontSizeSp.sp,
                fontWeight = if (config.isBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (config.isItalic) FontStyle.Italic else FontStyle.Normal,
                fontFamily = fontFamily,
                textAlign = textAlign,
                lineHeight = (config.fontSizeSp * 1.35f).sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 4. Watermark for Free Users
        val shouldShowWatermark = config.watermarkEnabled && !isPremium
        if (shouldShowWatermark) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "👑 StatusKing AI",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
