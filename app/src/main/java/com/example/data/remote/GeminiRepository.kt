package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GovernmentScheme
import com.example.data.model.IndianLanguage
import com.example.data.repository.SchemeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(private val schemeRepository: SchemeRepository) {

    private val apiService = GeminiApiClient.apiService

    suspend fun querySakhi(
        userVoiceText: String,
        language: IndianLanguage
    ): SakhiAdviceResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // Check if API key is missing or blank
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiRepository", "Using intelligent offline dialect engine (API key not provided).")
            return@withContext generateLocalDialectResponse(userVoiceText, language)
        }

        try {
            val systemPrompt = """
                You are 'Sakhi' (सखी), a trusted, loving elder sister and AI voice guide designed for first-time rural Indian women with zero digital knowledge and no tech background.
                
                The user has spoken in ${language.englishName} (${language.nativeName}) or their regional slang/dialect (such as Bhojpuri, Awadhi, Maithili, Marwari, Chennai/Madurai Tamil, Telangana Telugu, Vidarbha Marathi, Kathiyawadi Gujarati, etc.).
                
                YOUR MISSION:
                1. Understand their informal, dialectal spoken words, mispronunciations, and indirect descriptions (e.g., 'chulhe ka dhuan', 'delivery me kharcha', 'silai ka kaam', 'bachi ki shadi', 'loan chahiye dukan ke liye', 'koi pareshan kar raha hai').
                2. Respond warmly in ${language.nativeName} using simple, comforting colloquial spoken words.
                3. Structure your response into 4 crystal-clear spoken parts:
                   - स्नेही अभिवादन व सहानुभूति (Loving reassurance: 'दीदी, चिंता मत कीजिए...')
                   - योजना का नाम और मिलने वाला लाभ (Exact scheme name & direct cash/item benefit)
                   - जरूरी 2-3 कागजात (Simple documents: Aadhaar, Bank Passbook, Ration Card)
                   - गांव में कहाँ जाना है (Where to go: Anganwadi didi, Panchayat, CSC Jan Seva Kendra, or Post Office)
                4. Always remind them: 'यह योजना पूरी तरह मुफ्त है, किसी दलाल को पैसा मत दीजिए।'
                5. Keep it conversational, brief, and easily readable for Voice Text-to-Speech (under 120 words).
            """.trimIndent()

            val request = GeminiApiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = "User said: \"$userVoiceText\" in ${language.englishName}. Explain the right Indian government scheme for her in simple ${language.nativeName}.")),
                        role = "user"
                    )
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 600)
            )

            val response = apiService.generateContent(apiKey, request)
            val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!generatedText.isNullOrBlank()) {
                val matched = schemeRepository.findMatchingSchemes(userVoiceText)
                SakhiAdviceResult(
                    spokenAdvice = cleanMarkdownForSpeech(generatedText),
                    suggestedSchemes = matched.take(3),
                    isOffline = false
                )
            } else {
                generateLocalDialectResponse(userVoiceText, language)
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Gemini API error, falling back to local engine: ${e.message}")
            generateLocalDialectResponse(userVoiceText, language)
        }
    }

    private fun generateLocalDialectResponse(
        userVoiceText: String,
        language: IndianLanguage
    ): SakhiAdviceResult {
        val matched = schemeRepository.findMatchingSchemes(userVoiceText)
        val primaryScheme = matched.firstOrNull() ?: schemeRepository.getAllSchemes().first()

        val localizedAdvice = when (language) {
            IndianLanguage.HINDI -> """
                नमस्ते दीदी, बिल्कुल चिंता मत कीजिए। आपके लिए सबसे अच्छी योजना '${primaryScheme.title}' है।
                
                इसमें आपको ${primaryScheme.benefitHighlight} मिलता है।
                
                जरूरी कागज: ${primaryScheme.documentsNeeded.take(3).joinToString(", ")}।
                
                कहाँ जाना है: ${primaryScheme.whereToApply}
                
                सावधानी: ${primaryScheme.safetyWarning}
            """.trimIndent()

            IndianLanguage.TAMIL -> """
                வணக்கம் சகோதரி, கவலைப்படாதீர்கள். உங்களுக்கு ஏற்ற திட்டம் '${primaryScheme.title}' ஆகும்.
                
                இதில் உங்களுக்கு கிடைக்கும் நன்மை: ${primaryScheme.benefitHighlight}.
                
                தேவையான ஆவணங்கள்: ஆதார் அட்டை, வங்கி பாஸ்புக், ரேஷன் கார்டு.
                
                எங்கு செல்ல வேண்டும்: ${primaryScheme.whereToApply}
                
                எச்சரிக்கை: இந்த உதவி முற்றிலும் இலவசம். இடைத்தரகர்களுக்கு பணம் கொடுக்க வேண்டாம்.
            """.trimIndent()

            IndianLanguage.TELUGU -> """
                నమస్కారం అక్కా, ఏమాత్రం భయపడకండి. మీ కోసం ఉత్తమమైన పథకం '${primaryScheme.title}'.
                
                దీని ద్వారా మీకు లభించే ప్రయోజనం: ${primaryScheme.benefitHighlight}.
                
                కావలసిన పత్రాలు: ఆధార్ కార్డు, బ్యాంక్ పాస్‌బుక్, రేషన్ కార్డు.
                
                ఎక్కడికి వెళ్ళాలి: ${primaryScheme.whereToApply}
                
                హెచ్చరిక: ఈ పథకం పూర్తిగా ఉచితం, ఎవరికీ ఒక్క రూపాయి కూడా లంచం ఇవ్వకండి.
            """.trimIndent()

            IndianLanguage.BENGALI -> """
                নমস্কার দিদি, কোনো চিন্তা করবেন না। আপনার জন্য উপযুক্ত প্রকল্প হলো '${primaryScheme.title}'।
                
                এতে আপনি পাবেন: ${primaryScheme.benefitHighlight}।
                
                প্রয়োজনীয় নথি: আধার কার্ড, ব্যাংক পাসবই, রেশন কার্ড।
                
                কোথায় যাবেন: ${primaryScheme.whereToApply}
                
                সতর্কতা: এই সরকারি সুবিধা সম্পূর্ণ বিনামূল্যে পাওয়া যায়। কাউকে কোনো টাকা দেবেন না।
            """.trimIndent()

            IndianLanguage.MARATHI -> """
                नमस्कार ताई, काळजी करू नका. तुमच्यासाठी सर्वात उपयुक्त योजना '${primaryScheme.title}' ही आहे.
                
                यात तुम्हाला लाभ मिळेल: ${primaryScheme.benefitHighlight}.
                
                लागणारी कागदपत्रे: आधार कार्ड, बँक पासबुक, रेशन कार्ड.
                
                कुठे संपर्क करावा: ${primaryScheme.whereToApply}
                
                सावधानता: ही शासकीय योजना पूर्णपणे मोफत आहे, कोणालाही पैसे देऊ नका.
            """.trimIndent()

            IndianLanguage.GUJARATI -> """
                નમસ્તે બહેન, ચિંતા ન કરો. તમારા માટે સૌથી શ્રેષ્ઠ યોજના '${primaryScheme.title}' છે.
                
                આમાં મળતો લાભ: ${primaryScheme.benefitHighlight}.
                
                જરૂરી પુરાવા: આધાર કાર્ડ, બેંક પાસબુક, રેશન કાર્ડ.
                
                ક્યાં જવું: ${primaryScheme.whereToApply}
                
                સાવચેતી: આ યોજના એકદમ મફત છે, કોઈ પણ દલાલને પૈસા ન આપવા.
            """.trimIndent()

            IndianLanguage.KANNADA -> """
                ನಮಸ್ಕಾರ ಅಕ್ಕ, ಚಿಂತೆ ಮಾಡಬೇಡಿ. ನಿಮಗಾಗಿ ಸೂಕ್ತವಾದ ಸರ್ಕಾರಿ ಯೋಜನೆ '${primaryScheme.title}'.
                
                ಇದರಲ್ಲಿ ದೊರೆಯುವ ಲಾಭ: ${primaryScheme.benefitHighlight}.
                
                ಅಗತ್ಯ ದಾಖಲೆಗಳು: ಆಧಾರ್ ಕಾರ್ಡ್, ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್, ಪಡಿತರ ಚೀಟಿ.
                
                ಎಲ್ಲಿಗೆ ಹೋಗಬೇಕು: ${primaryScheme.whereToApply}
                
                ಎಚ್ಚರಿಕೆ: ಈ ಯೋಜನೆ ಸಂಪೂರ್ಣ ಉಚಿತವಾಗಿದೆ, ಯಾವುದೇ ಮಧ್ಯವರ್ತಿಗಳಿಗೆ ಹಣ ನೀಡಬೇಡಿ.
            """.trimIndent()

            IndianLanguage.MALAYALAM -> """
                നമസ്കാരം സഹോദരീ, വിഷമിക്കേണ്ടതില്ല. നിങ്ങൾക്കായി ഏറ്റവും അനുയോജ്യമായ പദ്ധതി '${primaryScheme.title}' ആണ്.
                
                ലഭിക്കുന്ന ആനുകൂല്യം: ${primaryScheme.benefitHighlight}.
                
                ആവശ്യമായ രേഖകൾ: ആധാർ കാർഡ്, ബാങ്ക് പാസ്സ്ബുക്ക്, റേഷൻ കാർഡ്.
                
                എവിടെ ബന്ധപ്പെടണം: ${primaryScheme.whereToApply}
                
                മുന്നറിയിപ്പ്: ഇത് പൂർണ്ണമായും സൗജന്യ പദ്ധതിയാണ്. ആർക്കും പണം നൽകരുത്.
            """.trimIndent()

            IndianLanguage.PUNJABI -> """
                ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਭੈਣ ਜੀ, ਫ਼ਿਕਰ ਨਾ ਕਰੋ। ਤੁਹਾਡੇ ਲਈ ਸਭ ਤੋਂ ਵਧੀਆ ਸਕੀਮ '${primaryScheme.title}' ਹੈ।
                
                ਇਸ ਵਿੱਚ ਮਿਲਣ ਵਾਲਾ ਲਾਭ: ${primaryScheme.benefitHighlight}.
                
                ਜ਼ਰੂਰੀ ਕਾਗਜ਼: ਆਧਾਰ ਕਾਰਡ, ਬੈਂਕ ਪਾਸਬੁੱਕ, ਰਾਸ਼ਨ ਕਾਰਡ।
                
                ਕਿੱਥੇ ਜਾਣਾ ਹੈ: ${primaryScheme.whereToApply}
                
                ਸਾਵਧਾਨੀ: ਇਹ ਸਰਕਾਰੀ ਸਕੀਮ ਬਿਲਕੁਲ ਮੁਫ਼ਤ ਹੈ, ਕਿਸੇ ਵਿਚੋਲੇ ਨੂੰ ਪੈਸੇ ਨਾ ਦਿਓ।
            """.trimIndent()

            IndianLanguage.ODIA -> """
                ନମସ୍କାର ଭଉଣୀ, ଚିନ୍ତା କରନ୍ତୁ ନାହିଁ। ଆପଣଙ୍କ ପାଇଁ ସଠିକ୍ ଯୋଜନା ହେଉଛି '${primaryScheme.title}'।
                
                ମିଳିବାକୁ ଥିବା ସୁବିଧା: ${primaryScheme.benefitHighlight}।
                
                ଆବଶ୍ୟକ କାଗଜପତ୍ର: ଆଧାର କାର୍ଡ, ବ୍ୟାଙ୍କ ପାସବୁକ୍, ରାସନ କାର୍ଡ।
                
                କେଉଁଠାକୁ ଯିବେ: ${primaryScheme.whereToApply}
                
                ସତର୍କତା: ଏହି ଯୋଜନା ସମ୍ପୂର୍ଣ୍ଣ ମାଗଣା, କାହାକୁ କିଛି ଟଙ୍କା ଦିଅନ୍ତୁ ନାହିଁ।
            """.trimIndent()

            IndianLanguage.ENGLISH -> """
                Hello sister, do not worry at all. The best scheme for you is '${primaryScheme.title}'.
                
                Benefits provided: ${primaryScheme.benefitHighlight}.
                
                Documents required: ${primaryScheme.documentsNeeded.take(3).joinToString(", ")}.
                
                Where to apply: ${primaryScheme.whereToApply}
                
                Warning: ${primaryScheme.safetyWarning}
            """.trimIndent()
        }

        return SakhiAdviceResult(
            spokenAdvice = localizedAdvice,
            suggestedSchemes = matched.take(3),
            isOffline = true
        )
    }

    private fun cleanMarkdownForSpeech(text: String): String {
        return text
            .replace(Regex("\\*\\*|\\*|_|#|`"), "")
            .replace(Regex("\n+"), "\n")
            .trim()
    }
}

data class SakhiAdviceResult(
    val spokenAdvice: String,
    val suggestedSchemes: List<GovernmentScheme>,
    val isOffline: Boolean
)
