package com.movie.binged.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.movie.binged.R

// 1️⃣ Define the font family using local font files
val CarlitoFontFamily = FontFamily(
    Font(
        resId = R.font.carlito_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.carlito_bold,
        weight = FontWeight.Bold
    )
)

// 2️⃣ Base Material 3 typography
private val baseline = Typography()

// 3️⃣ App typography using Carlito everywhere
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = CarlitoFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = CarlitoFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = CarlitoFontFamily),

    headlineLarge = baseline.headlineLarge.copy(fontFamily = CarlitoFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = CarlitoFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = CarlitoFontFamily),

    titleLarge = baseline.titleLarge.copy(fontFamily = CarlitoFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = CarlitoFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = CarlitoFontFamily),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = CarlitoFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = CarlitoFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = CarlitoFontFamily),

    labelLarge = baseline.labelLarge.copy(fontFamily = CarlitoFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = CarlitoFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = CarlitoFontFamily),
)
