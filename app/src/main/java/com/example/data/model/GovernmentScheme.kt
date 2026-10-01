package com.example.data.model

enum class SchemeCategory(val label: String, val iconName: String) {
    HEALTH_MATERNITY("मातृत्व व स्वास्थ्य", "favorite"),
    SKILLS_LIVELIHOOD("रोजगार व सिलाई", "work"),
    SHG_FINANCE("बचत गट व लोन", "account_balance"),
    CLEAN_FUEL_HOME("गैस चूल्हा व आवास", "home"),
    GIRL_CHILD("बेटी की पढ़ाई व भविष्य", "school"),
    SAFETY_RIGHTS("सुरक्षा व सहायता", "shield")
}

data class GovernmentScheme(
    val id: String,
    val title: String,
    val localizedTitle: String,
    val category: SchemeCategory,
    val benefitHighlight: String,
    val shortSummary: String,
    val eligibility: List<String>,
    val documentsNeeded: List<String>,
    val whereToApply: String,
    val helplineNumber: String,
    val voiceAudioText: String,
    val safetyWarning: String = "यह सरकारी योजना बिल्कुल मुफ़्त है। किसी दलाल को एक रुपया भी न दें।"
) {
    fun getLocalizedSpeechText(language: IndianLanguage): String {
        val docs = documentsNeeded.joinToString(", ")
        return when (language) {
            IndianLanguage.ENGLISH ->
                "Sister, here are the details for $localizedTitle. Key benefit: $benefitHighlight. Who can apply: $shortSummary. Required documents: $docs. Apply at: $whereToApply. Helpline number is $helplineNumber. This government scheme is completely free."

            IndianLanguage.HINDI ->
                "दीदी, $title की पूरी जानकारी सुनिए। मुख्य लाभ: $benefitHighlight। $shortSummary। जरूरी कागजात: $docs। आवेदन करने के लिए: $whereToApply। हेल्पलाइन नंबर है $helplineNumber। यह योजना बिल्कुल मुफ़्त है, किसी को पैसे न दें।"

            IndianLanguage.TAMIL ->
                "சகோதரி, $localizedTitle திட்டத்தின் விவரங்கள்: முக்கிய நன்மை: $benefitHighlight. விவரம்: $shortSummary. தேவையான ஆவணங்கள்: $docs. விண்ணப்பிக்க: $whereToApply. உதவி எண் $helplineNumber."

            IndianLanguage.TELUGU ->
                "అక్కా, $localizedTitle పథకం పూర్తి వివరాలు: ప్రధాన ప్రయోజనం: $benefitHighlight. వివరాలు: $shortSummary. అవసరమైన పత్రాలు: $docs. దరఖాస్తు కేంద్రం: $whereToApply. సహాయ నంబర్ $helplineNumber."

            IndianLanguage.BENGALI ->
                "দিদি, $localizedTitle প্রকল্পের বিবরণ: মূল সুবিধা: $benefitHighlight। বিবরণ: $shortSummary। প্রয়োজনীয় নথিপত্র: $docs। আবেদনের স্থান: $whereToApply। হেল্পলাইন নম্বর $helplineNumber।"

            IndianLanguage.MARATHI ->
                "ताई, $localizedTitle योजनेची संपूर्ण माहिती: मुख्य लाभ: $benefitHighlight. माहिती: $shortSummary. लागणारी कागदपत्रे: $docs. अर्ज करण्याचे ठिकाण: $whereToApply. हेल्पलाइन क्रमांक $helplineNumber."

            IndianLanguage.GUJARATI ->
                "બહેન, $localizedTitle યોજનાની માહિતી: મુખ્ય લાભ: $benefitHighlight. વિગત: $shortSummary. જરૂરી દસ્તાવેજ: $docs. અરજી સ્થળ: $whereToApply. હેલ્પલાઇન નંબર $helplineNumber."

            IndianLanguage.KANNADA ->
                "ಅಕ್ಕ, $localizedTitle ಯೋಜನೆಯ ಸಂಪೂರ್ಣ ವಿವರ: ಮುಖ್ಯ ಪ್ರಯೋಜನ: $benefitHighlight. ವಿವರ: $shortSummary. ಅಗತ್ಯ ದಾಖಲೆಗಳು: $docs. ಅರ್ಜಿ ಸಲ್ಲಿಸಲು: $whereToApply. ಸಹಾಯವಾಣಿ ಸಂಖ್ಯೆ $helplineNumber."

            IndianLanguage.MALAYALAM ->
                "സഹോദരി, $localizedTitle പദ്ധതിയുടെ വിവരങ്ങൾ: പ്രധാന ആനുകൂല്യം: $benefitHighlight. വിവരങ്ങൾ: $shortSummary. ആവശ്യമായ രേഖകൾ: $docs. അപേക്ഷിക്കേണ്ട സ്ഥലം: $whereToApply. ഹെൽപ്പ് ലൈൻ നമ്പർ $helplineNumber."

            IndianLanguage.PUNJABI ->
                "ਭੈਣ ਜੀ, $localizedTitle ਯੋਜਨਾ ਦੀ ਜਾਣਕਾਰੀ: ਮੁੱਖ ਲਾਭ: $benefitHighlight। ਲੋੜੀਂਦੇ ਕਾਗਜ਼ਾਤ: $docs। ਅਰਜ਼ੀ ਦੇਣ ਲਈ: $whereToApply। ਹੈਲਪਲਾਈਨ ਨੰਬਰ $helplineNumber।"

            IndianLanguage.ODIA ->
                "ଭଉଣୀ, $localizedTitle ଯୋଜନାର ସମ୍ପୂର୍ଣ୍ଣ ବିବରଣୀ: ମୁଖ୍ୟ ସୁବିଧା: $benefitHighlight। ଆବଶ୍ୟକ କାଗଜପତ୍ର: $docs। ଆବେଦନ କେନ୍ଦ୍ର: $whereToApply। ହେଲ୍ପଲାଇନ ନମ୍ବର $helplineNumber।"
        }
    }
}
