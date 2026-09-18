package com.statusking.ai.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

enum class StatusLanguage(val displayName: String, val nativeName: String, val code: String) {
    HINDI("Hindi", "हिंदी", "hi"),
    HINGLISH("Hinglish", "Hinglish", "hi-en"),
    ENGLISH("English", "English", "en"),
    BENGALI("Bengali", "বাংলা", "bn");

    companion object {
        fun fromString(name: String?): StatusLanguage {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: HINGLISH
        }
    }
}

enum class StatusTone(val displayName: String, val emoji: String) {
    ATTITUDE("Attitude", "😎"),
    LOVE("Love", "❤️"),
    FUNNY("Funny", "😂"),
    SAD("Sad", "💔"),
    MOTIVATIONAL("Motivational", "🔥"),
    ROMANTIC("Romantic", "🌹"),
    FRIENDSHIP("Friendship", "🤝"),
    FESTIVAL("Festival", "🪔"),
    SAVAGE("Savage", "⚡");

    companion object {
        fun fromString(name: String?): StatusTone {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: ATTITUDE
        }
    }
}

enum class StatusCategory(val displayName: String, val emoji: String) {
    ATTITUDE("Attitude", "😎"),
    LOVE("Love", "❤️"),
    SAD("Sad", "🥀"),
    FUNNY("Funny", "🤣"),
    FRIENDSHIP("Friendship", "👥"),
    MOTIVATION("Motivation", "💪"),
    SHAYARI("Shayari", "✍️"),
    GOOD_MORNING("Good Morning", "🌅"),
    GOOD_NIGHT("Good Night", "🌙"),
    FESTIVAL("Festival", "🎉"),
    BIRTHDAY("Birthday", "🎂"),
    SUCCESS("Success", "🏆"),
    LIFE("Life", "🌿"),
    BREAKUP("Breakup", "💔"),
    DESI("Desi", "🪕"),
    VIRAL("Viral", "🚀");

    companion object {
        fun fromString(name: String?): StatusCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: VIRAL
        }
    }
}

data class StatusItem(
    val id: Long = 0,
    val text: String,
    val category: String,
    val tone: String = StatusTone.ATTITUDE.displayName,
    val language: String = StatusLanguage.HINGLISH.displayName,
    val author: String = "StatusKing AI",
    val isFavorite: Boolean = false,
    val isCreation: Boolean = false,
    val creationImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class StatusTemplate(
    val id: String,
    val title: String,
    val category: StatusCategory,
    val quote: String,
    val gradientColors: List<Long>,
    val textColorHex: Long = 0xFFFFFFFF,
    val fontType: FontStyleType = FontStyleType.SANS_SERIF,
    val isPremium: Boolean = false,
    val decorativeEmoji: String = "✨"
)

enum class FontStyleType(val label: String, val fontFamily: FontFamily) {
    SANS_SERIF("Modern Sans", FontFamily.SansSerif),
    SERIF("Classic Serif", FontFamily.Serif),
    MONOSPACE("Retro Mono", FontFamily.Monospace),
    CURSIVE("Creative Cursive", FontFamily.Cursive)
}

data class PosterConfig(
    val text: String = "Apna time aayega nahi, apna time hum layenge!",
    val fontIndex: Int = 0,
    val fontSizeSp: Float = 24f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val textAlign: Int = 1, // 0: Left, 1: Center, 2: Right
    val textColor: Long = 0xFFFFFFFF,
    val gradientIndex: Int = 0,
    val imageUri: String? = null,
    val watermarkEnabled: Boolean = true,
    val emoji: String? = "✨",
    val blurBackground: Boolean = false,
    val textOffsetX: Float = 0f,
    val textOffsetY: Float = 0f,
    val rotationAngle: Float = 0f
)
