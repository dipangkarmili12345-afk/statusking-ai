package com.example

import com.statusking.ai.data.local.SampleData
import com.statusking.ai.model.StatusCategory
import com.statusking.ai.model.StatusLanguage
import com.statusking.ai.model.StatusTone
import com.statusking.ai.repository.DefaultAiRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusKingUnitTest {

    @Test
    fun testStarterStatusesExist() {
        assertTrue("Starter statuses should not be empty", SampleData.STARTER_STATUSES.isNotEmpty())
        assertTrue("At least 20 starter statuses for offline use", SampleData.STARTER_STATUSES.size >= 20)
    }

    @Test
    fun testStarterTemplatesExist() {
        assertTrue("Starter templates should not be empty", SampleData.STARTER_TEMPLATES.isNotEmpty())
    }

    @Test
    fun testAiGeneratorReturnsFiveStatusesOffline() = runBlocking {
        val repository = DefaultAiRepository()
        val result = repository.generateStatuses(
            topic = "Bhai ki attitude",
            language = StatusLanguage.HINGLISH,
            tone = StatusTone.ATTITUDE,
            category = StatusCategory.ATTITUDE
        )

        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertNotNull(list)
        assertEquals(5, list!!.size)

        list.forEach { status ->
            assertFalse(status.text.isBlank())
            assertEquals("Attitude", status.category)
            assertEquals("Attitude", status.tone)
        }
    }

    @Test
    fun testPromptBuilderContainsTopicAndLanguage() {
        val repository = DefaultAiRepository()
        val prompt = repository.buildStatusPrompt(
            topic = "Royal Chai",
            language = StatusLanguage.HINDI,
            tone = StatusTone.FRIENDSHIP,
            category = StatusCategory.DESI
        )

        assertTrue(prompt.contains("Royal Chai"))
        assertTrue(prompt.contains("Hindi"))
        assertTrue(prompt.contains("StatusKing AI"))
    }
}
