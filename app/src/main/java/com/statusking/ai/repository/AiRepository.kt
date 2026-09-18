package com.statusking.ai.repository

import com.statusking.ai.model.StatusCategory
import com.statusking.ai.model.StatusItem
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.model.StatusTone
import kotlinx.coroutines.delay
import kotlin.random.Random

interface AiRepository {
    suspend fun generateStatuses(
        topic: String,
        language: StatusLanguage,
        tone: StatusTone,
        category: StatusCategory
    ): Result<List<StatusItem>>

    fun buildStatusPrompt(
        topic: String,
        language: StatusLanguage,
        tone: StatusTone,
        category: StatusCategory
    ): String
}

class DefaultAiRepository : AiRepository {

    override fun buildStatusPrompt(
        topic: String,
        language: StatusLanguage,
        tone: StatusTone,
        category: StatusCategory
    ): String {
        return """
            You are StatusKing AI, an expert Indian social media status, Shayari, and meme creator.
            Create 5 viral, catchy, short status lines for WhatsApp/Instagram status.
            Topic: "$topic"
            Language: ${language.displayName} (${language.nativeName})
            Tone: ${tone.displayName} ${tone.emoji}
            Category: ${category.displayName}
            
            Guidelines:
            - Make each status unique, punchy, modern, and culturally resonant with Indian youth.
            - Include relevant emojis.
            - Keep length between 10 to 30 words per status.
            - Return exactly 5 numbered status lines, one per line.
        """.trimIndent()
    }

    override suspend fun generateStatuses(
        topic: String,
        language: StatusLanguage,
        tone: StatusTone,
        category: StatusCategory
    ): Result<List<StatusItem>> {
        // Simulating realistic AI generation latency
        delay(600)

        // If an external Gemini API key is configured via Secrets/BuildConfig, we could call:
        // Firebase.ai or Gemini GenerativeModel with buildStatusPrompt(...).
        // Since no hardcoded key is allowed and offline first is mandatory,
        // we use this robust, dynamic combinatorial generator engine.
        val generatedQuotes = generateDynamicStatuses(
            cleanTopic = topic.trim().ifEmpty { category.displayName },
            language = language,
            tone = tone,
            category = category
        )

        val items = generatedQuotes.mapIndexed { index, quote ->
            StatusItem(
                id = 0,
                text = quote,
                category = category.displayName,
                tone = tone.displayName,
                language = language.displayName,
                author = "StatusKing AI",
                isFavorite = false
            )
        }

        return Result.success(items)
    }

    private fun generateDynamicStatuses(
        cleanTopic: String,
        language: StatusLanguage,
        tone: StatusTone,
        category: StatusCategory
    ): List<String> {
        val results = mutableSetOf<String>()

        val templates = getTemplatesFor(language, tone)
        val shuffledTemplates = templates.shuffled()

        for (template in shuffledTemplates) {
            val formatted = template.replace("{topic}", cleanTopic)
            results.add(formatted)
            if (results.size >= 5) break
        }

        // Fillers if needed
        while (results.size < 5) {
            results.add(getFallbackLine(cleanTopic, language, tone, results.size))
        }

        return results.toList()
    }

    private fun getTemplatesFor(language: StatusLanguage, tone: StatusTone): List<String> {
        return when (language) {
            StatusLanguage.HINGLISH -> when (tone) {
                StatusTone.ATTITUDE, StatusTone.SAVAGE -> listOf(
                    "Naam yaad rakhna, kyunki '{topic}' pe humara rule chalta hai! 👑⚡",
                    "Apni value khud jaante hain, '{topic}' ke liye kisi aur ki approval nahi chahiye. 💯",
                    "Jalne wale bahut hain, par humara '{topic}' level hamesha alag hai. 🔥",
                    "Khamoshi se kaam karo, jab humara '{topic}' aayega to aawaz sabko sunai degi. 🦁",
                    "Bheed ka hissa nahi banna, hum wahi hain jahan '{topic}' ka flow shuru hota hai. 🚀",
                    "Sher shant baitha hai iska matlab shikar bhoola nahi. '{topic}' pe focus hai bas! 👑",
                    "Status to har koi lagata hai, par '{topic}' pe brand humara banta hai. 💥"
                )
                StatusTone.LOVE, StatusTone.ROMANTIC -> listOf(
                    "Dil ki har dhadkan sirf ek hi baat kehti hai, bas tum aur tumhara '{topic}'. ❤️",
                    "Tum meri wo subah ho jo har raat ke andhere ko haseen bana deti hai. '{topic}' 🌹",
                    "Kuch rishte lafzon ke mohtaaj nahi hote, jaise tum aur mera '{topic}'. 💖",
                    "Tere bina sab adhoora sa lagta hai, khaas kar jab baat '{topic}' ki ho. ✨",
                    "Zindagi mein hazaaron aaye gaye, par dil ko bas tumhara '{topic}' pasand aaya. 🕊️"
                )
                StatusTone.FUNNY -> listOf(
                    "Maine socha tha '{topic}' pe serious hounga, par neend aur biryani ne plan flop kar diya! 😂",
                    "Zindagi mein 2 cheezein time pe nahi milti: OTP aur '{topic}' ka mood! 🤣",
                    "Engineer hoon bhai, '{topic}' ko chhod ke duniya ke har issue ka jugaad hai mere paas! 📱",
                    "Kal se pakka '{topic}' pe control karunga... bas yeh aakhri baar! 🍕",
                    "Duniya gol hai aur mera '{topic}' ka plan hamesha roll hai! 🤪"
                )
                StatusTone.MOTIVATIONAL -> listOf(
                    "Koshish itni khamoshi se karo ki tumhari '{topic}' ki safalta shor macha de! 🔥",
                    "Mushkilein aayengi, par jab tak '{topic}' ka jazba zinda hai, koi hara nahi sakta! 💪",
                    "Rukna nahi hai, thakna nahi hai, '{topic}' jeet ke hi saans lena hai. 🏆",
                    "Sapne wo nahi jo sote huye dekhe jayein, sapne to '{topic}' jaise hain jo sone na dein. 🚀",
                    "Aaj ki mehnat hi kal tumhare '{topic}' ki pehchan banegi. ⏳"
                )
                StatusTone.FRIENDSHIP -> listOf(
                    "Yaar wo nahi jo roz milein, yaar wo hain jo '{topic}' ke time sab chhod ke daude chale aayein! 🤝",
                    "Dosti mein no formalities, bas ek cup chai aur '{topic}' ki baatein. ☕",
                    "Kismat walo ko milte hain aise dost jo har '{topic}' mein saath khade rahein. 🫂"
                )
                else -> listOf(
                    "Zindagi ka maza tab hai jab aap har pal '{topic}' ko dil se jiyo. ✨",
                    "Har naya din ek nayi umeed le kar aata hai, focus on '{topic}'! 🌅",
                    "Waqt badalega aur humara daur aayega, sabar aur '{topic}'. ⏳"
                )
            }

            StatusLanguage.HINDI -> when (tone) {
                StatusTone.ATTITUDE, StatusTone.SAVAGE -> listOf(
                    "हम वहां खड़े होते हैं जहां बात '{topic}' की और मैटर बड़े होते हैं। 👑",
                    "औकात की बात मत कर पगले, तेरी सोच से आगे हमारा '{topic}' का रुतबा है। 🔥",
                    "तेवर तो हम वक्त आने पर दिखाएंगे, शहर तुम खरीदो पर '{topic}' पर हुकूमत हमारी होगी। ⚡",
                    "खामोशियां बेवजह नहीं होतीं, बड़ा तूफान लाने की तैयारी है '{topic}' में। 🦁",
                    "किरदार ऐसा रखो कि लोग तारीफ के मोहताज न हों, हमारा '{topic}' ही काफी है। 💥"
                )
                StatusTone.LOVE, StatusTone.ROMANTIC -> listOf(
                    "इश्क़ वो नहीं जो दुनिया को जताया जाए, इश्क़ तो वो है जो '{topic}' में निभाया जाए। ❤️",
                    "तेरी एक मुस्कान पर मेरी पूरी कायनात कुर्बान है, तू ही मेरा '{topic}' है। 🌹",
                    "मोहब्बत में झुकना कोई ऐब नहीं, चमकता सूरज भी तो ढल जाता है '{topic}' के वास्ते। 💖",
                    "दिल की किताब में तेरा ही नाम लिखा है, सबसे हसीन पन्ना '{topic}' का है। ✨"
                )
                StatusTone.MOTIVATIONAL -> listOf(
                    "मंजिलें उन्हीं को मिलती हैं जिनके सपनों में जान होती है, '{topic}' से उड़ान होती है! 🚀",
                    "जीतने का असली मजा तब है जब सारी दुनिया आपके '{topic}' में हारने का इंतजार कर रही हो। 💪",
                    "रास्ते चाहे कितने भी कठिन हों, अगर '{topic}' का संकल्प दृढ़ है तो जीत पक्की है। 🔥",
                    "खुद पर भरोसा रखो, आज का संघर्ष कल तुम्हारे '{topic}' का स्वर्णिम इतिहास लिखेगा। 🏆"
                )
                StatusTone.FUNNY -> listOf(
                    "सोचा था आज '{topic}' पर मेहनत करूंगा, पर बिस्तर ने इतनी मोहब्बत से बुला लिया! 😂",
                    "जिंदगी में बस दो ही गम हैं: एक धीमा इंटरनेट और दूसरा '{topic}' का टेंशन! 🤣",
                    "सच्चे दोस्त वही होते हैं जो आपके '{topic}' पर भी दिल खोलकर हंसें! 🤪"
                )
                else -> listOf(
                    "सुख और दुख दोनों जीवन के रंग हैं, हर हाल में '{topic}' का आनंद लो। 🪔",
                    "हर सुबह नई उमंग लाती है, विश्वास रखो '{topic}' में। 🌅",
                    "रिश्ते वही सच्चे होते हैं जो वक्त के हर मोड़ पर '{topic}' निभाते हैं। 🤝"
                )
            }

            StatusLanguage.BENGALI -> when (tone) {
                StatusTone.ATTITUDE, StatusTone.SAVAGE -> listOf(
                    "আমার নীরবতাকে দুর্বলতা ভাবলে ভুল করবে, সিংহ গর্জন না করলেও '{topic}' এর রাজাই থাকে। 🦁",
                    "লোকের কথায় কান দিই না, নিজের যোগ্যতায় '{topic}' তৈরি করেছি। 👑",
                    "হিংসা করার লোক অনেক, কিন্তু আমার '{topic}' এর লেভেল আলাদা। ⚡",
                    "ভিড়ের অংশ হওয়ার চেয়ে নিজের স্বতন্ত্র '{topic}' গড়ে তোলাই শ্রেষ্ঠ। 💥",
                    "নামটা মনে রেখো, কারণ '{topic}' এর ব্র্যান্ড সবাই হতে পারে না। 🔥"
                )
                StatusTone.LOVE, StatusTone.ROMANTIC -> listOf(
                    "তুমি আমার জীবনের সবচেয়ে মিষ্টি অনুভূতি, আমার সব স্বপ্ন আর '{topic}' জুড়ে আছো। ❤️",
                    "এক কাপ চা আর তোমার সাথে কাটানো বিকেল, এর চেয়ে সুন্দর '{topic}' আর কিছু নেই। ☕🌹",
                    "হৃদয়ের প্রতিটি স্পন্দনে শুধুই তোমার নাম আর ভালোবাসা। '{topic}' 💖",
                    "তোমার চোখে চোখ রেখে হারিয়ে যেতে চাই, যেখানে শুধুই আমাদের '{topic}'। ✨"
                )
                StatusTone.MOTIVATIONAL -> listOf(
                    "হাল ছেড়ে দেওয়া কাপুরুষতা, লড়াই চালিয়ে যাওয়াই আসল '{topic}' এর বিজয়। 💪",
                    "স্বপ্ন সত্যি করতে হলে ঘুম ত্যাগ করে '{topic}' এর পেছনে ছুটতে হবে। 🚀",
                    "যতক্ষণ না তুমি নিজেকে বিশ্বাস করছো, সাফল্য আসবে না। '{topic}' এ বিশ্বাস রাখো। 🔥",
                    "আজকের কঠিন পরিশ্রমই কাল তোমার '{topic}' এর স্বর্ণযুগ নিয়ে আসবে। 🏆"
                )
                StatusTone.FUNNY -> listOf(
                    "ডায়েট করার কথা ভেবেছিলাম, কিন্তু রসগোল্লা আর বিরিয়ানি দেখে '{topic}' ভুলে গেলাম! 😂",
                    "ঘুম থেকে উঠতে দেরি আর রাতে ঘুমাতে দেরি, এভাবেই আমার '{topic}' চলছে! 🤣",
                    "ফেসবুকে জ্ঞান আর চায়ের দোকানে '{topic}', বাঙালির রক্তে মেশা! 🤪"
                )
                else -> listOf(
                    "নতুন ভোর নতুন আশা নিয়ে আসুক, সুন্দর হোক আজকের '{topic}'। 🌅",
                    "উৎসবের আনন্দে ভরে উঠুক প্রতিটি মন, সবার জীবনে আসুক '{topic}'। 🪔",
                    "বন্ধুত্ব হলো নিঃস্বার্থ বন্ধন, যেখানে কোনো শর্ত ছাড়া থাকে '{topic}'। 🤝"
                )
            }

            StatusLanguage.ENGLISH -> when (tone) {
                StatusTone.ATTITUDE, StatusTone.SAVAGE -> listOf(
                    "Remember my name, because in '{topic}', we don't follow rules, we set them. 👑",
                    "I don't need anyone's validation when it comes to '{topic}'. I know my worth. ⚡",
                    "Silence is the best answer to those who doubt your '{topic}'. Let results speak. 🦁",
                    "Not everyone can handle this energy. Keep watching my '{topic}'. 🔥",
                    "Level up so high that your '{topic}' becomes their daily topic of conversation. 🚀"
                )
                StatusTone.LOVE, StatusTone.ROMANTIC -> listOf(
                    "In a world full of temporary things, you are my perpetual '{topic}'. ❤️",
                    "Every love song makes complete sense now, all because of you and '{topic}'. 🌹",
                    "You hold a piece of my heart that no one else ever could. '{topic}' 💖",
                    "Together with you is my favorite place to be. Living our '{topic}'. ✨"
                )
                StatusTone.MOTIVATIONAL -> listOf(
                    "Push yourself, because no one else is going to do '{topic}' for you! 💪",
                    "Doubt kills more dreams than failure ever will. Believe in your '{topic}'! 🔥",
                    "Small daily improvements over time lead to massive '{topic}' results. 🏆",
                    "Don't stop when you're tired. Stop when you've conquered '{topic}'. 🚀"
                )
                StatusTone.FUNNY -> listOf(
                    "My life motto: Start '{topic}' on Monday. Which Monday? That remains unconfirmed! 😂",
                    "I’m not lazy, I’m just on energy-saving mode for '{topic}'. 🔋🤣",
                    "Running late is my cardio, especially when '{topic}' is involved! 🏃‍♂️"
                )
                else -> listOf(
                    "Start each day with a grateful heart and positive '{topic}' energy. 🌅",
                    "Cherish every moment, life is beautiful when celebrated through '{topic}'. ✨",
                    "True friends are the family you choose, forever bonded by '{topic}'. 🤝"
                )
            }
        }
    }

    private fun getFallbackLine(topic: String, language: StatusLanguage, tone: StatusTone, index: Int): String {
        return when (language) {
            StatusLanguage.HINDI -> "वक्त हमारा है और '$topic' में जीत भी हमारी होगी। ⚡"
            StatusLanguage.BENGALI -> "জীবনের প্রতিটি পদক্ষেপে '$topic' নিয়ে এগিয়ে চলো নির্ভীকভাবে। 🌟"
            StatusLanguage.ENGLISH -> "Master your mind and own your '$topic' journey! 👑"
            StatusLanguage.HINGLISH -> "Zindagi ka ek hi usool hai: Keep calm and conquer '$topic'! 🚀"
        }
    }
}
