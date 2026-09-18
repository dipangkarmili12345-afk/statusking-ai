package com.statusking.ai.data.local

import com.statusking.ai.model.FontStyleType
import com.statusking.ai.model.StatusCategory
import com.statusking.ai.model.StatusItem
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.model.StatusTemplate
import com.statusking.ai.model.StatusTone

object SampleData {

    val GRADIENTS: List<List<Long>> = listOf(
        listOf(0xFF4A00E0, 0xFF8E2DE2), // Royal Purple to Violet
        listOf(0xFFFF416C, 0xFFFF4B2B), // Sunset Crimson
        listOf(0xFF11998E, 0xFF38EF7D), // Emerald Chai
        listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364), // Deep Midnight
        listOf(0xFFFC466B, 0xFF3F5EFB), // Neon Disco
        listOf(0xFFF12711, 0xFFF5AF19), // Desi Saffron Sun
        listOf(0xFF141E30, 0xFF243B55), // Dark Royal Blue
        listOf(0xFF833ab4, 0xFFfd1d1d, 0xFFfcb045), // Insta Sunset
        listOf(0xFF23074D, 0xFFCC5333), // Velvet Wine
        listOf(0xFF1D2671, 0xFFC33764), // Twilight Magenta
        listOf(0xFF000000, 0xFF434343), // Sleek Noir
        listOf(0xFF5B247A, 0xFF1BCEDF)  // Cyber Violet Turquoise
    )

    val STARTER_TEMPLATES: List<StatusTemplate> = listOf(
        // 1. Attitude
        StatusTemplate(
            id = "tmpl_att_1",
            title = "Royal Savage",
            category = StatusCategory.ATTITUDE,
            quote = "Naam yaad rakhna, kyunki attitude sabke paas nahi hota. 👑",
            gradientColors = listOf(0xFF2D0B5A, 0xFF621093),
            textColorHex = 0xFFFFD700,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = false,
            decorativeEmoji = "👑"
        ),
        StatusTemplate(
            id = "tmpl_att_2",
            title = "Alpha Swagger",
            category = StatusCategory.ATTITUDE,
            quote = "Apni value khud jaanta hoon, duniya ki approval ki zarurat nahi. ⚡",
            gradientColors = listOf(0xFF0F0C29, 0xFF302B63, 0xFF24243E),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.MONOSPACE,
            isPremium = true,
            decorativeEmoji = "⚡"
        ),
        StatusTemplate(
            id = "tmpl_att_3",
            title = "Savage Bhai",
            category = StatusCategory.ATTITUDE,
            quote = "Jalne wale jalte rahein, hum apna parcham lehrate rahenge. 🔥",
            gradientColors = listOf(0xFFCC5333, 0xFF23074D),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = false,
            decorativeEmoji = "🔥"
        ),

        // 2. Love
        StatusTemplate(
            id = "tmpl_love_1",
            title = "Pyaar Ki Roshni",
            category = StatusCategory.LOVE,
            quote = "Tum meri wo khushi ho jise main lafzon mein bayaan nahi kar sakta. ❤️",
            gradientColors = listOf(0xFFED213A, 0xFF93291E),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.CURSIVE,
            isPremium = false,
            decorativeEmoji = "❤️"
        ),
        StatusTemplate(
            id = "tmpl_love_2",
            title = "Eternal Sunset",
            category = StatusCategory.LOVE,
            quote = "In your eyes, I found my forever home. 🌹",
            gradientColors = listOf(0xFFFF758C, 0xFFFF7EB3),
            textColorHex = 0xFF2D0B5A,
            fontType = FontStyleType.SERIF,
            isPremium = true,
            decorativeEmoji = "🌹"
        ),
        StatusTemplate(
            id = "tmpl_love_3",
            title = "Bengali Bhalobasha",
            category = StatusCategory.LOVE,
            quote = "তুমি আমার জীবনের সবচেয়ে সুন্দর অধ্যায়। 💖",
            gradientColors = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = false,
            decorativeEmoji = "💖"
        ),

        // 3. Funny
        StatusTemplate(
            id = "tmpl_fun_1",
            title = "Monday Mood",
            category = StatusCategory.FUNNY,
            quote = "Dieting shuru ki thi kal, par samosa ne aake gale laga liya! 😂",
            gradientColors = listOf(0xFFFFA07A, 0xFFFF6347),
            textColorHex = 0xFF1A1A1A,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = false,
            decorativeEmoji = "🥟"
        ),
        StatusTemplate(
            id = "tmpl_fun_2",
            title = "Engineer Life",
            category = StatusCategory.FUNNY,
            quote = "Neend aati nahi aur subah uthna bardasht hota nahi. Mast chal raha hai sab! 😴",
            gradientColors = listOf(0xFF11998E, 0xFF38EF7D),
            textColorHex = 0xFF0D3B2E,
            fontType = FontStyleType.MONOSPACE,
            isPremium = false,
            decorativeEmoji = "🤣"
        ),
        StatusTemplate(
            id = "tmpl_fun_3",
            title = "Wifi Connection",
            category = StatusCategory.FUNNY,
            quote = "Relationship status: Waiting for slow wifi to load my food order. 📱",
            gradientColors = listOf(0xFF3A6073, 0xFF3A7BD5),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = true,
            decorativeEmoji = "🍕"
        ),

        // 4. Shayari
        StatusTemplate(
            id = "tmpl_shay_1",
            title = "Ghalib Vibes",
            category = StatusCategory.SHAYARI,
            quote = "हज़ारों ख्वाहिशें ऐसी कि हर ख्वाहिश पे दम निकले, बहुत निकले मेरे अरमान लेकिन फिर भी कम निकले। ✨",
            gradientColors = listOf(0xFF1E130C, 0xFF9A8478),
            textColorHex = 0xFFFFECC8,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "✍️"
        ),
        StatusTemplate(
            id = "tmpl_shay_2",
            title = "Dard-e-Dil",
            category = StatusCategory.SHAYARI,
            quote = "Kuch baatein unkahi hi achhi lagti hain, har baat ka ailaan zaroori nahi hota. 🥀",
            gradientColors = listOf(0xFF2C3E50, 0xFF4CA1AF),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SERIF,
            isPremium = true,
            decorativeEmoji = "🥀"
        ),
        StatusTemplate(
            id = "tmpl_shay_3",
            title = "Bengali Kobita",
            category = StatusCategory.SHAYARI,
            quote = "মেঘের দেশে একলা মন, বৃষ্টি ভেজা প্রতিটি ক্ষণ। 🌧️",
            gradientColors = listOf(0xFF0F2027, 0xFF2C5364),
            textColorHex = 0xFFE0F7FA,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "🌧️"
        ),

        // 5. Motivation
        StatusTemplate(
            id = "tmpl_mot_1",
            title = "Hustle Hard",
            category = StatusCategory.MOTIVATION,
            quote = "Koshish karne walon ki kabhi haar nahi hoti! 🔥",
            gradientColors = listOf(0xFFF12711, 0xFFF5AF19),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = false,
            decorativeEmoji = "🔥"
        ),
        StatusTemplate(
            id = "tmpl_mot_2",
            title = "Empire Building",
            category = StatusCategory.MOTIVATION,
            quote = "Work in silence, let your roaring success make the noise. 🏆",
            gradientColors = listOf(0xFF141E30, 0xFF243B55),
            textColorHex = 0xFFFFD700,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = true,
            decorativeEmoji = "🏆"
        ),
        StatusTemplate(
            id = "tmpl_mot_3",
            title = "Lakshya Bhed",
            category = StatusCategory.MOTIVATION,
            quote = "सपने वो नहीं जो हम सोते हुए देखते हैं, सपने वो हैं जो हमें सोने नहीं देते। 🚀",
            gradientColors = listOf(0xFF4B1248, 0xFFF0C27B),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "🚀"
        ),

        // 6. Festival
        StatusTemplate(
            id = "tmpl_fest_1",
            title = "Diwali Glow",
            category = StatusCategory.FESTIVAL,
            quote = "May this festival bring endless joy, prosperity and divine light to your family! 🪔",
            gradientColors = listOf(0xFFB20A2C, 0xFFFFFBD5),
            textColorHex = 0xFF580516,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "🪔"
        ),
        StatusTemplate(
            id = "tmpl_fest_2",
            title = "Holi Rangotsav",
            category = StatusCategory.FESTIVAL,
            quote = "Rang barse khushiyon ke! Wishing you a vibrant, joyous festival! 🎨",
            gradientColors = listOf(0xFFFF007F, 0xFF7928CA, 0xFF00DFD8),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = true,
            decorativeEmoji = "🎨"
        ),
        StatusTemplate(
            id = "tmpl_fest_3",
            title = "Durga Puja Shubho",
            category = StatusCategory.FESTIVAL,
            quote = "শুভ শারদীয়ার প্রীতি ও আন্তরিক শুভেচ্ছা! মা দুর্গার আশীর্বাদে জীবন আলোকময় হোক। 🔱",
            gradientColors = listOf(0xFFD31027, 0xFFEA384D),
            textColorHex = 0xFFFFE082,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "🔱"
        ),

        // 7. Good Morning
        StatusTemplate(
            id = "tmpl_gm_1",
            title = "Subah Ki Kiran",
            category = StatusCategory.GOOD_MORNING,
            quote = "Subah ki nayi dhoop aapki zindagi mein nayi umeed le kar aaye. Suprabhat! 🌅",
            gradientColors = listOf(0xFFFF8008, 0xFFFFC837),
            textColorHex = 0xFF3D1E03,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "☕"
        ),
        StatusTemplate(
            id = "tmpl_gm_2",
            title = "Fresh Chai Sunrise",
            category = StatusCategory.GOOD_MORNING,
            quote = "A warm cup of cutting chai and a heart full of gratitude. Good Morning! ☀️",
            gradientColors = listOf(0xFF56AB2F, 0xFFA8E063),
            textColorHex = 0xFF0F380A,
            fontType = FontStyleType.SANS_SERIF,
            isPremium = true,
            decorativeEmoji = "☀️"
        ),

        // 8. Good Night
        StatusTemplate(
            id = "tmpl_gn_1",
            title = "Meethe Sapne",
            category = StatusCategory.GOOD_NIGHT,
            quote = "Sitaron bhari raat aapko meethi neend aur sukoon bakhshe. Shubh Ratri! 🌙",
            gradientColors = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
            textColorHex = 0xFFE0EAFC,
            fontType = FontStyleType.SERIF,
            isPremium = false,
            decorativeEmoji = "🌙"
        ),
        StatusTemplate(
            id = "tmpl_gn_2",
            title = "Moonlit Peace",
            category = StatusCategory.GOOD_NIGHT,
            quote = "Leave all worries behind. Tomorrow is a brand new page. Sleep tight. ✨",
            gradientColors = listOf(0xFF141E30, 0xFF243B55),
            textColorHex = 0xFFFFFFFF,
            fontType = FontStyleType.CURSIVE,
            isPremium = true,
            decorativeEmoji = "⭐"
        )
    )

    val STARTER_STATUSES: List<StatusItem> = listOf(
        // Attitude
        StatusItem(
            id = 1,
            text = "हम वहां खड़े होते हैं जहां मैटर बड़े होते हैं। 😎",
            category = StatusCategory.ATTITUDE.displayName,
            tone = StatusTone.ATTITUDE.displayName,
            language = StatusLanguage.HINDI.displayName,
            isFavorite = true
        ),
        StatusItem(
            id = 2,
            text = "Apna standard itna high rakho ki log comparison karna chhod dein. ⚡",
            category = StatusCategory.ATTITUDE.displayName,
            tone = StatusTone.SAVAGE.displayName,
            language = StatusLanguage.HINGLISH.displayName,
            isFavorite = false
        ),
        StatusItem(
            id = 3,
            text = "আমার নীরবতাকে দুর্বলতা ভাবলে ভুল করবে, সিংহ গর্জন না করলেও রাজাই থাকে। 🦁",
            category = StatusCategory.ATTITUDE.displayName,
            tone = StatusTone.ATTITUDE.displayName,
            language = StatusLanguage.BENGALI.displayName,
            isFavorite = true
        ),
        StatusItem(
            id = 4,
            text = "I don't compete with anyone. I am my own competition. 👑",
            category = StatusCategory.ATTITUDE.displayName,
            tone = StatusTone.ATTITUDE.displayName,
            language = StatusLanguage.ENGLISH.displayName,
            isFavorite = false
        ),

        // Love
        StatusItem(
            id = 5,
            text = "Teri muskaan hi meri har subah ki wajah hai. ❤️",
            category = StatusCategory.LOVE.displayName,
            tone = StatusTone.ROMANTIC.displayName,
            language = StatusLanguage.HINGLISH.displayName,
            isFavorite = true
        ),
        StatusItem(
            id = 6,
            text = "इश्क़ वो नहीं जो दुनिया को दिखाया जाए, इश्क़ वो है जो दिल से निभाया जाए। 🌹",
            category = StatusCategory.LOVE.displayName,
            tone = StatusTone.LOVE.displayName,
            language = StatusLanguage.HINDI.displayName,
            isFavorite = false
        ),
        StatusItem(
            id = 7,
            text = "তোমার চোখের ওই মায়ায় হারিয়ে যেতে চাই বারবার। 💖",
            category = StatusCategory.LOVE.displayName,
            tone = StatusTone.ROMANTIC.displayName,
            language = StatusLanguage.BENGALI.displayName,
            isFavorite = true
        ),

        // Funny
        StatusItem(
            id = 8,
            text = "Subah ki neend aur raat ka khana, dono se compromise nahi ho sakta! 😂",
            category = StatusCategory.FUNNY.displayName,
            tone = StatusTone.FUNNY.displayName,
            language = StatusLanguage.HINGLISH.displayName,
            isFavorite = false
        ),
        StatusItem(
            id = 9,
            text = "कुछ लोग इतनी शिद्दत से झूठ बोलते हैं कि खुद भी यकीन करने लगते हैं! 🤣",
            category = StatusCategory.FUNNY.displayName,
            tone = StatusTone.FUNNY.displayName,
            language = StatusLanguage.HINDI.displayName,
            isFavorite = false
        ),

        // Motivation
        StatusItem(
            id = 10,
            text = "जीतने का असली मज़ा तब आता है जब सब आपके हारने का इंतज़ार कर रहे हों। 🔥",
            category = StatusCategory.MOTIVATION.displayName,
            tone = StatusTone.MOTIVATIONAL.displayName,
            language = StatusLanguage.HINDI.displayName,
            isFavorite = true
        ),
        StatusItem(
            id = 11,
            text = "Your only limit is your mind. Don't stop until you're proud. 🚀",
            category = StatusCategory.MOTIVATION.displayName,
            tone = StatusTone.MOTIVATIONAL.displayName,
            language = StatusLanguage.ENGLISH.displayName,
            isFavorite = false
        ),
        StatusItem(
            id = 12,
            text = "হাল ছেড়ে দেওয়া কাপুরুষতা, লড়াই চালিয়ে যাওয়াই আসল বীরত্ব। 💪",
            category = StatusCategory.MOTIVATION.displayName,
            tone = StatusTone.MOTIVATIONAL.displayName,
            language = StatusLanguage.BENGALI.displayName,
            isFavorite = false
        ),

        // Friendship
        StatusItem(
            id = 13,
            text = "Dosti mein no sorry, no thank you, bas 2 plate momos khila de bhai! 🤝",
            category = StatusCategory.FRIENDSHIP.displayName,
            tone = StatusTone.FRIENDSHIP.displayName,
            language = StatusLanguage.HINGLISH.displayName,
            isFavorite = true
        ),

        // Shayari
        StatusItem(
            id = 14,
            text = "मोहब्बत में झुकना कोई ऐब नहीं, चमकता सूरज भी तो ढल जाता है चांद के वास्ते। ✍️",
            category = StatusCategory.SHAYARI.displayName,
            tone = StatusTone.ROMANTIC.displayName,
            language = StatusLanguage.HINDI.displayName,
            isFavorite = false
        ),

        // Good Morning / Good Night
        StatusItem(
            id = 15,
            text = "Nayi subah, naye khwab, aur ek kadak cup chai! Shubh Prabhat. ☕",
            category = StatusCategory.GOOD_MORNING.displayName,
            tone = StatusTone.MOTIVATIONAL.displayName,
            language = StatusLanguage.HINGLISH.displayName,
            isFavorite = false
        ),
        StatusItem(
            id = 16,
            text = "রাত কেটে ভোর হবে, নতুন দিনে নতুন আশা নিয়ে শুরু করো। শুভ রাত্রি! 🌙",
            category = StatusCategory.GOOD_NIGHT.displayName,
            tone = StatusTone.SAD.displayName,
            language = StatusLanguage.BENGALI.displayName,
            isFavorite = false
        )
    )
}
