package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.local.SchemeBookmarkEntity
import com.example.data.model.GovernmentScheme
import com.example.data.model.IndianLanguage
import com.example.data.model.SchemeCategory
import kotlinx.coroutines.flow.Flow

class SchemeRepository(private val bookmarkDao: BookmarkDao) {

    val allBookmarks: Flow<List<SchemeBookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun getBookmarkFlow(schemeId: String): Flow<SchemeBookmarkEntity?> =
        bookmarkDao.getBookmarkFlow(schemeId)

    suspend fun toggleBookmark(scheme: GovernmentScheme, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            bookmarkDao.deleteBookmark(scheme.id)
        } else {
            bookmarkDao.insertBookmark(
                SchemeBookmarkEntity(
                    schemeId = scheme.id,
                    schemeTitle = scheme.title,
                    benefitHighlight = scheme.benefitHighlight,
                    category = scheme.category.name
                )
            )
        }
    }

    suspend fun updateDocumentCheck(schemeId: String, docId: String, isChecked: Boolean) {
        val existing = bookmarkDao.getBookmark(schemeId)
        val currentSet = existing?.checkedDocumentsJson
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toMutableSet() ?: mutableSetOf()

        if (isChecked) {
            currentSet.add(docId)
        } else {
            currentSet.remove(docId)
        }

        val updatedJson = currentSet.joinToString(",")
        if (existing == null) {
            val scheme = getSchemeById(schemeId)
            bookmarkDao.insertBookmark(
                SchemeBookmarkEntity(
                    schemeId = schemeId,
                    schemeTitle = scheme?.title ?: schemeId,
                    benefitHighlight = scheme?.benefitHighlight ?: "",
                    category = scheme?.category?.name ?: SchemeCategory.HEALTH_MATERNITY.name,
                    checkedDocumentsJson = updatedJson
                )
            )
        } else {
            bookmarkDao.updateCheckedDocuments(schemeId, updatedJson)
        }
    }

    fun getAllSchemes(): List<GovernmentScheme> = listOf(
        GovernmentScheme(
            id = "pm_matru_vandana",
            title = "प्रधानमंत्री मातृ वंदना योजना (PMMVY)",
            localizedTitle = "Pradhan Mantri Matru Vandana Yojana",
            category = SchemeCategory.HEALTH_MATERNITY,
            benefitHighlight = "₹5,000 से ₹6,000 सीधे आपके बैंक खाते में",
            shortSummary = "गर्भवती और स्तनपान कराने वाली माताओं को पौष्टिक आहार और आराम के लिए सरकार सीधे बैंक खाते में आर्थिक सहायता देती है। पहले बच्चे पर ₹5,000 और दूसरी कन्या संतान होने पर ₹6,000 मिलते हैं।",
            eligibility = listOf(
                "गर्भवती महिला या 19 वर्ष से अधिक उम्र की नई मां",
                "सरकारी नौकरी में न हो",
                "परिवार की वार्षिक आय ₹8 लाख से कम हो या राशन कार्ड धारक हों",
                "आंगनवाड़ी केंद्र में गर्भावस्था का पंजीकरण (MCP कार्ड) अनिवार्य"
            ),
            documentsNeeded = listOf(
                "मां और पिता का आधार कार्ड (Aadhaar Card)",
                "मां का बैंक पासबुक (Aadhaar-linked DBT Account)",
                "MCP कार्ड (आंगनवाड़ी से बना ममता कार्ड)",
                "बच्चे का जन्म प्रमाण पत्र (दूसरी किस्त के लिए)",
                "राशन कार्ड या आय प्रमाण पत्र"
            ),
            whereToApply = "अपने गांव के निकटतम आंगनवाड़ी केंद्र (Anganwadi Didi) या प्राथमिक स्वास्थ्य केंद्र (PHC) में फॉर्म भरें।",
            helplineNumber = "104 / 14408",
            voiceAudioText = "दीदी, प्रधानमंत्री मातृ वंदना योजना में गर्भवती महिला को सीधे बैंक में 5000 से 6000 रुपये मिलते हैं। इसके लिए अपने गांव की आंगनवाड़ी दीदी के पास ममता कार्ड और आधार कार्ड लेकर जाइए। यह बिल्कुल मुफ़्त है।"
        ),
        GovernmentScheme(
            id = "pm_ujjwala_yojana",
            title = "प्रधानमंत्री उज्ज्वला योजना (PMUY)",
            localizedTitle = "Free LPG Gas Connection Scheme",
            category = SchemeCategory.CLEAN_FUEL_HOME,
            benefitHighlight = "मुफ्त गैस कनेक्शन + पहला सिलेंडर और चूल्हा फ्री",
            shortSummary = "ग्रामीण महिलाओं को चूल्हे के हानिकारक धुएं से मुक्ति दिलाने के लिए भारत सरकार द्वारा पूरी तरह से निःशुल्क एलपीजी गैस कनेक्शन और चूल्हा प्रदान किया जाता है। साथ ही ₹300 प्रति सिलेंडर सब्सिडी सीधे खाते में मिलती है।",
            eligibility = listOf(
                "महिला की आयु 18 वर्ष या उससे अधिक होनी चाहिए",
                "परिवार में पहले से कोई एलपीजी कनेक्शन नहीं होना चाहिए",
                "BPL, SC/ST, अंत्योदय (AAY) या ग्रामीण गरीब परिवार की महिला",
                "राशन कार्ड में परिवार के सदस्यों का नाम दर्ज हो"
            ),
            documentsNeeded = listOf(
                "महिला का आधार कार्ड और फोटो",
                "राशन कार्ड (Ration Card)",
                "बैंक खाता पासबुक (DBT सक्रिय)",
                "निवास का प्रमाण (Panchayat Certificate)"
            ),
            whereToApply = "निकटतम गैस एजेंसी (Indane, BharatGas, HP Gas) या जन सेवा केंद्र (CSC) में जाकर Ujjwala 2.0 फॉर्म भरें।",
            helplineNumber = "1906 / 1800-266-6696",
            voiceAudioText = "दीदी, उज्ज्वला योजना में महिलाओं को मुफ्त गैस सिलेंडर और चूल्हा मिलता है। अपनी नजदीकी गैस एजेंसी में राशन कार्ड और आधार कार्ड ले जाकर फॉर्म भरें। किसी को कोई पैसा न दें।"
        ),
        GovernmentScheme(
            id = "lakhpati_didi",
            title = "लखपति दीदी योजना व महिला स्वयं सहायता समूह",
            localizedTitle = "Lakhpati Didi & NRLM Women SHG",
            category = SchemeCategory.SHG_FINANCE,
            benefitHighlight = "₹1 लाख से ₹5 लाख तक बिना गारंटी लोन + स्किल ट्रेनिंग",
            shortSummary = "ग्रामीण महिलाओं को आर्थिक रूप से आत्मनिर्भर बनाकर उनकी सालाना आय ₹1 लाख से अधिक करने की योजना। सिलाई, डेयरी, मशरूम, ड्रोन दीदी, किराना, अगरबत्ती आदि के लिए शून्य या कम ब्याज पर पूंजी दी जाती है।",
            eligibility = listOf(
                "गांव के किसी महिला स्वयं सहायता समूह (SHG) की सदस्य हों",
                "उम्र 18 से 55 वर्ष के बीच हो",
                "कोई छोटा व्यवसाय या कृषि आधारित काम शुरू करने की इच्छा हो",
                "समूह में नियमित बैठक और बचत का रिकॉर्ड हो"
            ),
            documentsNeeded = listOf(
                "आधार कार्ड और 2 पासपोर्ट फोटो",
                "समूह का बैंक पासबुक और व्यक्तिगत पासबुक",
                "स्वयं सहायता समूह (SHG) का अनुमोदन पत्र",
                "निवास प्रमाण पत्र"
            ),
            whereToApply = "अपने गांव के SHG समूह की अध्यक्ष (दीदी), ग्राम संगठन (VO), या ब्लॉक विकास कार्यालय (BDO/NRLM मिशन) से संपर्क करें।",
            helplineNumber = "1800-11-2005",
            voiceAudioText = "दीदी, लखपति दीदी योजना में स्वयं सहायता समूह से जुड़ी महिलाओं को दुकान, सिलाई, या खेती के लिए बिना गारंटी बहुत कम ब्याज पर लोन और ट्रेनिंग मिलती है, ताकि आपकी सालाना कमाई एक लाख से ऊपर हो।"
        ),
        GovernmentScheme(
            id = "pm_vishwakarma_silai",
            title = "मुफ्त सिलाई मशीन व पीएम विश्वकर्मा योजना",
            localizedTitle = "Free Sewing Machine & PM Vishwakarma",
            category = SchemeCategory.SKILLS_LIVELIHOOD,
            benefitHighlight = "₹15,000 सिलाई किट वाउचर + ₹500/दिन प्रशिक्षण स्टाइपेंड",
            shortSummary = "दर्जी, शिल्पकार और सिलाई का काम करने वाली ग्रामीण महिलाओं को आधुनिक सिलाई मशीन खरीदने के लिए ₹15,000 का ई-वाउचर और 5 दिन की फ्री मास्टर ट्रेनिंग के साथ ₹500 प्रतिदिन का स्टाइपेंड दिया जाता है। बाद में ₹3 लाख तक कम ब्याज लोन भी मिलता है।",
            eligibility = listOf(
                "महिला की आयु कम से कम 18 वर्ष हो",
                "सिलाई या बुनाई का काम जानती हों या सीखने की इच्छुक हों",
                "परिवार में कोई सरकारी कर्मचारी न हो",
                "परिवार में एक ही सदस्य को यह लाभ मिल सकता है"
            ),
            documentsNeeded = listOf(
                "आधार कार्ड (मोबाइल नंबर लिंक होना चाहिए)",
                "बैंक खाता पासबुक",
                "राशन कार्ड या कारीगर पहचान पत्र",
                "पासपोर्ट साइज फोटो"
            ),
            whereToApply = "गांव के जन सेवा केंद्र (CSC Center) पर जाकर बायोमेट्रिक से मुफ्त पंजीकरण करवाएं। ग्राम प्रधान/पंचायत सचिव से सत्यापन होता है।",
            helplineNumber = "1800-267-7777",
            voiceAudioText = "दीदी, सिलाई का काम करने के लिए पीएम विश्वकर्मा में 15000 रुपये का वाउचर सिलाई मशीन के लिए मिलता है, और 5 दिन की ट्रेनिंग में हर दिन 500 रुपये मिलते हैं। सीएससी केंद्र पर जाकर मुफ्त में नाम दर्ज करवाएं।"
        ),
        GovernmentScheme(
            id = "sukanya_samriddhi",
            title = "सुकन्या समृद्धि योजना (SSY)",
            localizedTitle = "Sukanya Samriddhi Girl Child Scheme",
            category = SchemeCategory.GIRL_CHILD,
            benefitHighlight = "8.2% उच्चतम ब्याज + टैक्स छूट + बेटी का भविष्य सुरक्षित",
            shortSummary = "बेटी की उच्च शिक्षा और विवाह के लिए सरकार की सबसे सुरक्षित बचत योजना। मात्र ₹250 से खाता खुलवा सकते हैं। 21 वर्ष पूरे होने पर लाखों रुपये की गारंटीड परिपक्वता राशि मिलती है।",
            eligibility = listOf(
                "10 वर्ष से कम उम्र की बेटी के नाम पर माता-पिता खाता खोल सकते हैं",
                "एक परिवार में अधिकतम 2 बेटियों के लिए खाता मान्य (जुड़वां में 3)",
                "सालाना न्यूनतम ₹250 और अधिकतम ₹1.5 लाख जमा कर सकते हैं"
            ),
            documentsNeeded = listOf(
                "बेटी का जन्म प्रमाण पत्र (Birth Certificate)",
                "माता या पिता का आधार कार्ड और पैन कार्ड",
                "निवास का प्रमाण (Ration card / Electricity bill)",
                "बेटी और माता-पिता की 2 फोटो"
            ),
            whereToApply = "निकटतम डाकघर (Post Office) या किसी भी सरकारी बैंक (SBI, PNB, आदि) में जाकर सुकन्या खाता खोलें।",
            helplineNumber = "1800-266-6868",
            voiceAudioText = "दीदी, अगर आपके घर में 10 साल से छोटी बेटी है, तो डाकघर में मात्र 250 रुपये से सुकन्या खाता खुलवाइए। इस पर 8.2 प्रतिशत ब्याज मिलता है जो बेटी की 18 और 21 साल की उम्र में पढ़ाई और शादी में बहुत काम आएगा।"
        ),
        GovernmentScheme(
            id = "mudra_mahila_udyami",
            title = "प्रधानमंत्री मुद्रा योजना (महिला उद्यमी)",
            localizedTitle = "PM Mudra Loan for Women Entrepreneurs",
            category = SchemeCategory.SHG_FINANCE,
            benefitHighlight = "₹50,000 से ₹10 लाख तक लोन बिना किसी गिरवी के",
            shortSummary = "महिलाएं जो ब्यूटी पार्लर, बुटीक, पापड़-अचार, किराने की दुकान या कोई भी छोटा धंधा शुरू करना चाहती हैं, उन्हें बिना किसी जमीन या गहने को गिरवी रखे आसान किस्तों पर बैंक से लोन मिलता है।",
            eligibility = listOf(
                "18 वर्ष से अधिक उम्र की भारतीय महिला",
                "किसी बैंक का पुराना डिफॉल्टर न हो",
                "शिशु लोन: ₹50,000 तक (नया काम शुरू करने के लिए)",
                "किशोर लोन: ₹50,000 से ₹5 लाख तक",
                "तरुण लोन: ₹5 लाख से ₹10 लाख तक"
            ),
            documentsNeeded = listOf(
                "आधार कार्ड, वोटर कार्ड या पैन कार्ड",
                "बैंक खाता पिछले 6 महीने का स्टेटमेंट",
                "बिजनेस का साधारण प्लान या कोटेशन",
                "पासपोर्ट साइज 2 फोटो"
            ),
            whereToApply = "अपने नजदीकी किसी भी सरकारी बैंक (SBI, PNB, Baroda) या ग्रामीण बैंक (RRB) की शाखा में संपर्क करें।",
            helplineNumber = "1800-180-1111",
            voiceAudioText = "दीदी, अगर आप खुद का कोई छोटा काम जैसे ब्यूटी पार्लर, सिलाई सेंटर, या दुकान खोलना चाहती हैं, तो मुद्रा योजना में बिना कुछ गिरवी रखे 50000 रुपये से लेकर 5 लाख तक का लोन बैंक से मिल सकता है।"
        ),
        GovernmentScheme(
            id = "janani_suraksha",
            title = "जननी सुरक्षा योजना (JSY)",
            localizedTitle = "Janani Suraksha Yojana - Safe Childbirth",
            category = SchemeCategory.HEALTH_MATERNITY,
            benefitHighlight = "अस्पताल में डिलीवरी पर ₹1,400 नकद + मुफ्त एम्बुलेंस",
            shortSummary = "सरकारी अस्पताल में सुरक्षित प्रसव को बढ़ावा देने के लिए ग्रामीण क्षेत्र की गर्भवती महिलाओं को अस्पताल में बच्चा होने पर ₹1,400 की नकद सहायता, मुफ्त भोजन, दवाएं और 102/108 एम्बुलेंस की मुफ्त सुविधा दी जाती है।",
            eligibility = listOf(
                "ग्रामीण क्षेत्र की सभी गर्भवती महिलाएं जो सरकारी अस्पताल में प्रसव कराती हैं",
                "BPL और SC/ST परिवार की महिलाएं",
                "19 वर्ष या उससे अधिक उम्र"
            ),
            documentsNeeded = listOf(
                "मां का आधार कार्ड",
                "ममता कार्ड / MCP कार्ड",
                "बैंक पासबुक",
                "BPL राशन कार्ड (यदि उपलब्ध हो)"
            ),
            whereToApply = "गांव की आशा दीदी (ASHA Worker) या प्राथमिक स्वास्थ्य केंद्र (PHC/CHC)।",
            helplineNumber = "108 / 102 (मुफ्त एम्बुलेंस)",
            voiceAudioText = "दीदी, जननी सुरक्षा योजना में सरकारी अस्पताल में बच्चे के जन्म पर 1400 रुपये नकद और मुफ्त दवा, जांच व घर आने-जाने के लिए 102 एम्बुलेंस मुफ्त मिलती है। आशा दीदी से संपर्क करें।"
        ),
        GovernmentScheme(
            id = "women_helpline_181",
            title = "महिला हेल्पलाइन 181 व वन स्टॉप सेंटर (सखी)",
            localizedTitle = "181 Women Helpline & Sakhi One Stop Centre",
            category = SchemeCategory.SAFETY_RIGHTS,
            benefitHighlight = "24 घंटे मुफ्त गोपनीय मदद, कानूनी सलाह, पुलिस व आश्रय",
            shortSummary = "यदि किसी भी महिला या किशोरी को घरेलू हिंसा, दहेज प्रताड़ना, छेड़छाड़, साइबर अपराध, या किसी भी संकट का सामना करना पड़ रहा हो, तो 181 पर 24 घंटे किसी भी भाषा में बिल्कुल मुफ्त और पूरी तरह गुप्त मदद दी जाती है।",
            eligibility = listOf(
                "भारत की कोई भी महिला या किशोरी जो किसी भी प्रकार की परेशानी या हिंसा का सामना कर रही हो",
                "किसी कागजात या पैसे की कोई आवश्यकता नहीं है",
                "आपकी पहचान पूरी तरह गुप्त रखी जाती है"
            ),
            documentsNeeded = listOf(
                "किसी भी दस्तावेज की आवश्यकता नहीं है! केवल फोन घुमाएं।"
            ),
            whereToApply = "सीधे अपने फोन से 181 डायल करें, या जिले के 'वन स्टॉप सेंटर (सखी केंद्र)' में जाएं।",
            helplineNumber = "181 (टोल-फ्री 24x7) / 112 (आपातकालीन)",
            voiceAudioText = "दीदी, अगर कोई आपको परेशान कर रहा है, घर में मारपीट या कोई संकट है, तो बिना डरे 181 नंबर मिलाएं। यह 24 घंटे मुफ्त है और आपकी बात पूरी तरह गुप्त रखी जाएगी। कोई दस्तावेज नहीं चाहिए।"
        )
    )

    fun getSchemeById(id: String): GovernmentScheme? =
        getAllSchemes().firstOrNull { it.id == id }

    fun findMatchingSchemes(query: String): List<GovernmentScheme> {
        val q = query.lowercase().trim()
        if (q.isBlank()) return getAllSchemes()

        // Slang & Dialect Recognition Rules
        val matched = getAllSchemes().filter { scheme ->
            val matchId = when (scheme.id) {
                "pm_matru_vandana" -> listOf(
                    "bacha", "baccha", "delivery", "garbh", "garbhvati", "pregnan", "maternity", "6000", "5000",
                    "pilla", "kuzhandhai", "balantpan", "prosob", "janan", "dabbulu", "rupaye"
                ).any { q.contains(it) }
                "pm_ujjwala_yojana" -> listOf(
                    "gas", "cylinder", "chulha", "dhua", "silender", "ujjwala", "indane", "bharat", "lpg", "shegdi",
                    "aduppu", "gasu", "dhuyan"
                ).any { q.contains(it) }
                "lakhpati_didi" -> listOf(
                    "lakhpati", "bachat", "samuh", "shg", "sangha", "swayam", "karj", "rin", "loan", "group",
                    "kudumbashree", "dwakra", "didi", "kamai", "business", "dukan"
                ).any { q.contains(it) }
                "pm_vishwakarma_silai" -> listOf(
                    "silai", "machine", "sewing", "darji", "vishwakarma", "tailor", "kapda", "holige", "thayal",
                    "sivan", "15000", "voucher", "training", "stipend"
                ).any { q.contains(it) }
                "sukanya_samriddhi" -> listOf(
                    "sukanya", "beti", "ladki", "bachi", "kanya", "shadi", "shaadi", "padhai", "school", "daughter",
                    "post office", "dakghar", "magal", "ammayi", "porashona"
                ).any { q.contains(it) }
                "mudra_mahila_udyami" -> listOf(
                    "mudra", "parlour", "parlor", "kirana", "dukan", "dhandho", "rojar", "start", "dhandha", "crore",
                    "lakh", "udyami"
                ).any { q.contains(it) }
                "janani_suraksha" -> listOf(
                    "janani", "hospital", "aspatal", "phc", "asha", "ambulance", "108", "102", "1400"
                ).any { q.contains(it) }
                "women_helpline_181" -> listOf(
                    "helpline", "181", "madad", "police", "suraksha", "pareshan", "marpeet", "hinsa", "darr",
                    "emergency", "sos", "shikayat", "crisis"
                ).any { q.contains(it) }
                else -> false
            }

            matchId || scheme.title.lowercase().contains(q) ||
                    scheme.benefitHighlight.lowercase().contains(q) ||
                    scheme.shortSummary.lowercase().contains(q)
        }

        return if (matched.isNotEmpty()) matched else getAllSchemes()
    }
}
