package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SakhiDatabase
import com.example.data.model.IndianLanguage
import com.example.data.repository.SchemeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var schemeRepository: SchemeRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val db = SakhiDatabase.getDatabase(context)
        schemeRepository = SchemeRepository(db.bookmarkDao())
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Sakhi AI", appName)
    }

    @Test
    fun `test all indian languages are properly defined`() {
        val languages = IndianLanguage.entries
        assertTrue(languages.size >= 11)
        languages.forEach { lang ->
            assertTrue(lang.nativeName.isNotBlank())
            assertTrue(lang.voiceGreeting.isNotBlank())
            assertNotNull(lang.ttsLocale)
            assertTrue(lang.regionalSlangHints.isNotEmpty())
        }
    }

    @Test
    fun `test regional slang and dialect matching for schemes`() {
        val ujjwalaMatch = schemeRepository.findMatchingSchemes("चूल्हा धुआं देता है, गैस सिलेंडर")
        assertTrue(ujjwalaMatch.any { it.id == "pm_ujjwala_yojana" })

        val matruMatch = schemeRepository.findMatchingSchemes("बच्चा होने पर 6000 रुपये")
        assertTrue(matruMatch.any { it.id == "pm_matru_vandana" })

        val silaiMatch = schemeRepository.findMatchingSchemes("सिलाई मशीन और ट्रेनिंग")
        assertTrue(silaiMatch.any { it.id == "pm_vishwakarma_silai" })

        val shgMatch = schemeRepository.findMatchingSchemes("लखपति दीदी बचत गट लोन")
        assertTrue(shgMatch.any { it.id == "lakhpati_didi" })

        val helplineMatch = schemeRepository.findMatchingSchemes("181 महिला हेल्पलाइन आपातकालीन")
        assertTrue(helplineMatch.any { it.id == "women_helpline_181" })
    }

    @Test
    fun `test essential schemes have documents and helplines`() {
        val allSchemes = schemeRepository.getAllSchemes()
        assertTrue(allSchemes.isNotEmpty())
        allSchemes.forEach { scheme ->
            assertTrue(scheme.documentsNeeded.isNotEmpty())
            assertTrue(scheme.whereToApply.isNotBlank())
            assertTrue(scheme.helplineNumber.isNotBlank())
            assertTrue(scheme.voiceAudioText.isNotBlank())
        }
    }

    @Test
    fun `test scheme localized speech text generation for all languages`() {
        val scheme = schemeRepository.getAllSchemes().first()
        IndianLanguage.entries.forEach { lang ->
            val speech = scheme.getLocalizedSpeechText(lang)
            assertTrue(speech.isNotBlank())
            assertTrue(speech.contains(scheme.helplineNumber))
        }
    }

    @Test
    fun `test all indian languages have complete localized ui strings`() {
        IndianLanguage.entries.forEach { lang ->
            val ui = com.example.data.model.UiTextProvider.get(lang)
            assertTrue(ui.home.isNotBlank())
            assertTrue(ui.secureCall.isNotBlank())
            assertTrue(ui.saved.isNotBlank())
            assertTrue(ui.speakInDialect.isNotBlank())
            assertTrue(ui.schemeDetailsTitle.isNotBlank())
            assertTrue(ui.selectLanguageTitle.isNotBlank())
        }
    }
}
