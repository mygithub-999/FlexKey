package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class KeyType {
    CHARACTER,
    SHIFT,
    DELETE,
    ENTER,
    SPACE,
    LAYOUT_SWITCH,
    EMOJI_SWITCH,
    SETTINGS_SWITCH,
    ACTION
}

data class KeyModel(
    val keyCode: Int,
    val label: String,
    val secondaryLabel: String? = null,
    val keyType: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f
)

enum class KeyboardLayoutType {
    QWERTY,
    QWERTZ,
    AZERTY,
    SYMBOLS_PAGE_1,
    SYMBOLS_PAGE_2,
    EMOJI,
    NUMERIC
}

enum class ShiftState {
    OFF,
    SHIFTED_ONCE,
    CAPS_LOCKED
}

enum class KeyboardTheme(
    val title: String,
    val backgroundColor: Color,
    val keyColor: Color,
    val keyPressedColor: Color,
    val keyTextColor: Color,
    val keySecondaryTextColor: Color,
    val accentColor: Color,
    val candidateBgColor: Color,
    val candidateTextColor: Color,
    val isDark: Boolean
) {
    MATERIAL_YOU(
        title = "Material You Dynamic",
        backgroundColor = Color(0xFF1C1B1F),
        keyColor = Color(0xFF2B292F),
        keyPressedColor = Color(0xFF49454F),
        keyTextColor = Color(0xFFE6E1E5),
        keySecondaryTextColor = Color(0xFFCAC4D0),
        accentColor = Color(0xFFD0BCFF),
        candidateBgColor = Color(0xFF211F26),
        candidateTextColor = Color(0xFFD0BCFF),
        isDark = true
    ),
    OLED_BLACK(
        title = "AMOLED Pitch Black",
        backgroundColor = Color(0xFF000000),
        keyColor = Color(0xFF121212),
        keyPressedColor = Color(0xFF2A2A2A),
        keyTextColor = Color(0xFFFFFFFF),
        keySecondaryTextColor = Color(0xFF888888),
        accentColor = Color(0xFF38BDF8),
        candidateBgColor = Color(0xFF0D0D0D),
        candidateTextColor = Color(0xFF38BDF8),
        isDark = true
    ),
    CYBERPUNK_NEON(
        title = "Cyberpunk Neon",
        backgroundColor = Color(0xFF0D0221),
        keyColor = Color(0xFF19053A),
        keyPressedColor = Color(0xFF2C0B5E),
        keyTextColor = Color(0xFF00F5D4),
        keySecondaryTextColor = Color(0xFFFF007F),
        accentColor = Color(0xFFFF007F),
        candidateBgColor = Color(0xFF12032E),
        candidateTextColor = Color(0xFF00F5D4),
        isDark = true
    ),
    LIGHT_ELEVATED(
        title = "Light Clean",
        backgroundColor = Color(0xFFF1F5F9),
        keyColor = Color(0xFFFFFFFF),
        keyPressedColor = Color(0xFFE2E8F0),
        keyTextColor = Color(0xFF0F172A),
        keySecondaryTextColor = Color(0xFF64748B),
        accentColor = Color(0xFF2563EB),
        candidateBgColor = Color(0xFFE2E8F0),
        candidateTextColor = Color(0xFF1E40AF),
        isDark = false
    ),
    PASTEL_BREEZE(
        title = "Pastel Breeze",
        backgroundColor = Color(0xFFF5F3FF),
        keyColor = Color(0xFFFFFFFF),
        keyPressedColor = Color(0xFFDDD6FE),
        keyTextColor = Color(0xFF4C1D95),
        keySecondaryTextColor = Color(0xFF8B5CF6),
        accentColor = Color(0xFFEC4899),
        candidateBgColor = Color(0xFFEDE9FE),
        candidateTextColor = Color(0xFF6D28D9),
        isDark = false
    ),
    RETRO_AMBER(
        title = "Retro Terminal Amber",
        backgroundColor = Color(0xFF1A120B),
        keyColor = Color(0xFF2C1D11),
        keyPressedColor = Color(0xFF3E2A18),
        keyTextColor = Color(0xFFFFB100),
        keySecondaryTextColor = Color(0xFFD5CEA3),
        accentColor = Color(0xFFFFB100),
        candidateBgColor = Color(0xFF22150A),
        candidateTextColor = Color(0xFFFFC000),
        isDark = true
    )
}

data class Candidate(
    val word: String,
    val score: Float = 1.0f,
    val source: CandidateSource = CandidateSource.DICTIONARY,
    val originalQuery: String = ""
)

enum class CandidateSource {
    DICTIONARY,
    USER_SHORTCUT,
    AUTOCORRECT,
    AI_SMART
}
