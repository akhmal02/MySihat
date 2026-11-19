package com.example.mysihat.data

import androidx.compose.runtime.mutableStateOf

enum class AppLanguage {
    BM, EN
}

object LanguageManager {
    val currentLanguage = mutableStateOf(AppLanguage.BM)

    val isEnglish: Boolean
        get() = currentLanguage.value == AppLanguage.EN

    fun toggleLanguage() {
        currentLanguage.value = if (currentLanguage.value == AppLanguage.BM) AppLanguage.EN else AppLanguage.BM
    }

    // Pure localized string helper
    fun t(bm: String, en: String): String {
        return if (isEnglish) en else bm
    }
}
