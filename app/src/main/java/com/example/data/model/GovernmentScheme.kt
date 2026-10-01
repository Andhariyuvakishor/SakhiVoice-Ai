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
)
