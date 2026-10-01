package com.example.data.model

import java.util.Locale

enum class IndianLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val voiceGreeting: String,
    val ttsLocale: Locale,
    val recognitionCode: String,
    val regionalSlangHints: List<String>,
    val zeroLiteracyAudioPrompt: String
) {
    HINDI(
        code = "hi",
        nativeName = "हिंदी",
        englishName = "Hindi",
        voiceGreeting = "नमस्ते दीदी! मैं आपकी सखी हूँ। अपनी भाषा या बोली में बेझिझक बोलिए, मैं सरकारी योजना पाने में आपकी मदद करूँगी।",
        ttsLocale = Locale("hi", "IN"),
        recognitionCode = "hi-IN",
        regionalSlangHints = listOf(
            "चूल्हा धुआं देता है, गैस सिलेंडर चाहिए",
            "बच्चा होने वाला है, 6000 रुपये कैसे मिलेंगे?",
            "सिलाई मशीन का पैसा या ट्रेनिंग",
            "स्वयं सहायता समूह / लखपति दीदी लोन",
            "बेटी की शादी या पढ़ाई के लिए सुकन्या",
            "घर बनाने के लिए प्रधानमंत्री आवास",
            "दीदी कोई परेशानी है, महिला हेल्पलाइन"
        ),
        zeroLiteracyAudioPrompt = "नीचे बड़ा माइक बटन दबाकर बोलिए। मैं सुनकर बताऊँगी।"
    ),
    TAMIL(
        code = "ta",
        nativeName = "தமிழ்",
        englishName = "Tamil",
        voiceGreeting = "வணக்கம் சகோதரி! நான் உங்கள் சகி. உங்கள் சொந்த பாஷையிலேயே பேசுங்கள், அரசு உதவிகளை பெற நான் உதவுகிறேன்.",
        ttsLocale = Locale("ta", "IN"),
        recognitionCode = "ta-IN",
        regionalSlangHints = listOf(
            "குழந்தை பிறப்பு உதவித் தொகை ரூ 6000",
            "தையல் இயந்திரம் இலவச உதவி",
            "பெண் குழந்தை படிப்புக்கு சுகன்யா திட்டம்",
            "சுய உதவி குழு லோன் மற்றும் தொழில் உதவி",
            "மகளிர் உரிமைத் தொகை உதவி",
            "அவசர உதவி 181 எண்"
        ),
        zeroLiteracyAudioPrompt = "கீழே உள்ள பெரிய மைக் பொத்தானை அழுத்திப் பேசுங்கள்."
    ),
    TELUGU(
        code = "te",
        nativeName = "తెలుగు",
        englishName = "Telugu",
        voiceGreeting = "నమస్కారం అక్కా! నేను మీ సఖిని. మీ సమస్యను మీ సొంత యాసలో మాట్లాడండి, ప్రభుత్వ పథకాలతో మీకు సాయం చేస్తాను.",
        ttsLocale = Locale("te", "IN"),
        recognitionCode = "te-IN",
        regionalSlangHints = listOf(
            "గ్యాస్ సిలిండర్ ఉజ్వల పథకం",
            "డెలివరీ కి 6000 రూపాయల మాతృ వందన",
            "కుట్టు మిషన్ సహాయం మరియు శిక్షణ",
            "డ్వాక్రా సంఘం లోన్ / లఖ్‌పతి దీదీ",
            "ఆడపిల్ల చదువుకు సుకన్య సమృద్ధి",
            "మహిళా హెల్ప్‌లైన్ 181"
        ),
        zeroLiteracyAudioPrompt = "కింద ఉన్న పెద్ద మైక్ బటన్ నొక్కి మాట్లాడండి."
    ),
    BENGALI(
        code = "bn",
        nativeName = "বাংলা",
        englishName = "Bengali",
        voiceGreeting = "নমস্কার দিদি! আমি আপনার সখী। আপনার নিজের ভাষায় বলুন, যেকোনো সরকারি প্রকল্পের সুবিধা পেতে আমি সাহায্য করব।",
        ttsLocale = Locale("bn", "IN"),
        recognitionCode = "bn-IN",
        regionalSlangHints = listOf(
            "উজ্জ্বলা বিনামূল্যে গ্যাস সিলিন্ডার",
            "মাতৃ বন্দনা গর্ভাবস্থায় ৬০০০ টাকা",
            "বিনামূল্যে সেলাই মেশিন ও বিশ্বকর্মা",
            "স্বনির্ভর দল ও লাখপতি দিদি ঋণ",
            "কন্যাশ্রী ও সুকন্যা সমৃদ্ধি যোজনা",
            "মহিলা হেল্পলাইন ১৮১"
        ),
        zeroLiteracyAudioPrompt = "নিচের বড় মাইক বোতাম টিপে কথা বলুন।"
    ),
    MARATHI(
        code = "mr",
        nativeName = "मराठी",
        englishName = "Marathi",
        voiceGreeting = "नमस्कार ताई! मी तुमची सखी आहे. तुमच्या गावरान बोलीत बोला, तुम्हाला योग्य ती सरकारी योजना शोधून देईन.",
        ttsLocale = Locale("mr", "IN"),
        recognitionCode = "mr-IN",
        regionalSlangHints = listOf(
            "उज्ज्वला मोफत गॅस शेगडी व टाकी",
            "बाळंतपणाचे ६००० रुपये कसे मिळवायचे?",
            "मोफत शिलाई मशीन व विश्वकर्मा योजना",
            "महिला बचत गट व लखपती दीदी कर्ज",
            "मुलीच्या शिक्षणासाठी सुकन्या योजना",
            "महिला तक्रार निवारण १८१"
        ),
        zeroLiteracyAudioPrompt = "खालील मोठे माइक बटण दाबून बोला."
    ),
    GUJARATI(
        code = "gu",
        nativeName = "ગુજરાતી",
        englishName = "Gujarati",
        voiceGreeting = "નમસ્તે બહેન! હું તમારી સખી છું. તમારી કાઠિયાવાડી કે સુરતી બોલીમાં બોલો, હું સરકારી યોજના શોધવામાં મદદ કરીશ.",
        ttsLocale = Locale("gu", "IN"),
        recognitionCode = "gu-IN",
        regionalSlangHints = listOf(
            "મફત ગેસ સિલિન્ડર ઉજ્જવલા યોજના",
            "ડિલિવરી વખતે મળતા ૬૦૦૦ રૂપિયા",
            "મફત સિલાઈ મશીન યોજના",
            "મહિલા સખી મંડળ ધિરાણ લોન",
            "દીકરી માટે સુકન્યા સમૃદ્ધિ ખાતું",
            "મહિલા હેલ્પલાઇન ૧૮૧"
        ),
        zeroLiteracyAudioPrompt = "નીચેનું મોટું માઇક બટન દબાવીને બોલો."
    ),
    KANNADA(
        code = "kn",
        nativeName = "ಕನ್ನಡ",
        englishName = "Kannada",
        voiceGreeting = "ನಮಸ್ಕಾರ ಅಕ್ಕ! ನಾನು ನಿಮ್ಮ ಸಖಿ. ನಿಮ್ಮದೇ ಆಡುಭಾಷೆಯಲ್ಲಿ ಮಾತನಾಡಿ, ಸರ್ಕಾರಿ ಯೋಜನೆಗಳನ್ನು ಪಡೆಯಲು ನಾನು ನೆರವಾಗುತ್ತೇನೆ.",
        ttsLocale = Locale("kn", "IN"),
        recognitionCode = "kn-IN",
        regionalSlangHints = listOf(
            "ಉಜ್ವಲ ಉಚಿತ ಗ್ಯಾಸ್ ಸಿಲಿಂಡರ್",
            "ಹೆರಿಗೆ ಸಹಾಯಧನ ೬೦೦೦ ರೂಪಾಯಿ",
            "ಉಚಿತ ಹೊಲಿಗೆ ಯಂತ್ರ ಮತ್ತು ತರಬೇತಿ",
            "ಸ್ತ್ರೀಶಕ್ತಿ ಸಂಘ ಮತ್ತು ಲಖ್‌ಪತಿ ದೀದಿ ಸಾಲ",
            "ಹೆಣ್ಣು ಮಗುವಿನ ಶಿಕ್ಷಣಕ್ಕೆ ಸುಕನ್ಯಾ ಯೋಜನೆ",
            "ಮಹಿಳಾ ಸಹಾಯವಾಣಿ ೧೮೧"
        ),
        zeroLiteracyAudioPrompt = "ಕೆಳಗಿನ ದೊಡ್ಡ ಮೈಕ್ ಬಟನ್ ಒತ್ತಿ ಮಾತನಾಡಿ."
    ),
    MALAYALAM(
        code = "ml",
        nativeName = "മലയാളം",
        englishName = "Malayalam",
        voiceGreeting = "നമസ്കാരം സഹോദരീ! ഞാൻ നിങ്ങളുടെ സഖി. നിങ്ങളുടെ സ്വന്തം ഭാഷയിൽ സംസാരിക്കൂ, സർക്കാർ പദ്ധതികൾ ലഭിക്കാൻ ഞാൻ സഹായിക്കാം.",
        ttsLocale = Locale("ml", "IN"),
        recognitionCode = "ml-IN",
        regionalSlangHints = listOf(
            "ഉജ്ജ്വല സൗജന്യ പാചകവാതകം",
            "പ്രസവ ആനുകൂല്യം ൬൦൦൦ രൂപ",
            "തയ്യൽ മെഷീൻ സഹായം",
            "കുടുംബശ്രീ വായ്പയും സംരംഭകത്വവും",
            "പെൺകുട്ടികൾക്കായി സുകന്യ സമൃദ്ധി",
            "വനിതാ ഹെൽപ്പ് ലൈൻ 181"
        ),
        zeroLiteracyAudioPrompt = "താഴെയുള്ള വലിയ മൈക്ക് ബട്ടൺ അമർത്തി സംസാരിക്കുക."
    ),
    PUNJABI(
        code = "pa",
        nativeName = "ਪੰਜਾਬੀ",
        englishName = "Punjabi",
        voiceGreeting = "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਭੈਣ ਜੀ! ਮੈਂ ਤੁਹਾਡੀ ਸਖੀ ਹਾਂ। ਆਪਣੀ ਮਾਂ-ਬੋਲੀ ਵਿੱਚ ਦੱਸੋ, ਮੈਂ ਸਰਕਾਰੀ ਸਕੀਮਾਂ ਲੈਣ ਵਿੱਚ ਪੂਰੀ ਮਦਦ ਕਰਾਂਗੀ।",
        ttsLocale = Locale("pa", "IN"),
        recognitionCode = "pa-IN",
        regionalSlangHints = listOf(
            "ਮੁਫ਼ਤ ਗੈਸ ਸਿਲੰਡਰ ਉੱਜਵਲਾ ਸਕੀਮ",
            "ਬੱਚੇ ਦੇ ਜਨਮ 'ਤੇ ੬੦੦੦ ਰੁਪਏ ਮਾਤਰੂ ਵੰਦਨਾ",
            "ਮੁਫ਼ਤ ਸਿਲਾਈ ਮਸ਼ੀਨ ਅਤੇ ਟ੍ਰੇਨਿੰਗ",
            "ਸਵੈ-ਸਹਾਇਤਾ ਗਰੁੱਪ ਲਖਪਤੀ ਦੀਦੀ ਕਰਜ਼ਾ",
            "ਧੀ ਦੀ ਪੜ੍ਹਾਈ ਲਈ ਸੁਕੰਨਿਆ ਸਕੀਮ",
            "ਮਹਿਲਾ ਹੈਲਪਲਾਈਨ ੧੮੧"
        ),
        zeroLiteracyAudioPrompt = "ਹੇਠਾਂ ਦਿੱਤਾ ਵੱਡਾ ਮਾਈਕ ਬਟਨ ਦਬਾ ਕੇ ਬੋਲੋ।"
    ),
    ODIA(
        code = "or",
        nativeName = "ଓଡ଼ିଆ",
        englishName = "Odia",
        voiceGreeting = "ନମସ୍କାର ଭଉଣୀ! ମୁଁ ଆପଣଙ୍କ ସଖୀ। ଆପଣଙ୍କ ନିଜ ଭାଷାରେ କୁହନ୍ତୁ, ସରକାରୀ ଯୋଜନା ପାଇବାରେ ମୁଁ ସାହାଯ୍ୟ କରିବି।",
        ttsLocale = Locale("or", "IN"),
        recognitionCode = "or-IN",
        regionalSlangHints = listOf(
            "ମାଗଣା ଗ୍ୟାସ ସିଲିଣ୍ଡର ଉଜ୍ଜ୍ୱଳା",
            "ମାତୃ ବନ୍ଦନା ୬୦୦୦ ଟଙ୍କା ସହାୟତା",
            "ମାଗଣା ସିଲେଇ ମେସିନ ସହାୟତା",
            "ମିଶନ ଶକ୍ତି ଏବଂ ଲକ୍ଷପତି ଦିଦି ଋଣ",
            "ଝିଅ ପାଠପଢା ପାଇଁ ସୁକନ୍ୟା ସମୃଦ୍ଧି",
            "ମହିଳା ହେଲ୍ପଲାଇନ ୧୮୧"
        ),
        zeroLiteracyAudioPrompt = "ତଳେ ଥିବା ବଡ଼ ମାଇକ୍ ବଟନ୍ ଦବାଇ କଥା କୁହନ୍ତୁ।"
    ),
    ENGLISH(
        code = "en",
        nativeName = "English",
        englishName = "English",
        voiceGreeting = "Hello sister! I am your Sakhi. Speak in any regional language, dialect, or English, and I will help you claim government schemes.",
        ttsLocale = Locale("en", "IN"),
        recognitionCode = "en-IN",
        regionalSlangHints = listOf(
            "Free LPG Gas Cylinder Ujjwala Scheme",
            "Pregnant Mother ₹6,000 Cash Matru Vandana",
            "Free Sewing Machine Scheme & Training",
            "Self Help Group Lakhpati Didi Loan",
            "Girl Child Savings Sukanya Samriddhi",
            "Women Emergency Helpline 181"
        ),
        zeroLiteracyAudioPrompt = "Tap the big mic button below and speak."
    );

    companion object {
        fun fromCode(code: String): IndianLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: HINDI
        }
    }
}
