package com.example.tiltballgame.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.tiltballgame.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bagelFont = GoogleFont("Bagel Fat One")
val bagelFamily = FontFamily(
    Font(
        bagelFont,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        fontProvider = provider
    )
)

val cherryFont = GoogleFont("Cherry Bomb One")
val cherryFamily = FontFamily(
    Font(
        cherryFont,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        fontProvider = provider
    )
)

// Set of Material typography styles to start with
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontSize = 36.sp,
        fontFamily =bagelFamily,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontSize = 24.sp,
        fontFamily =bagelFamily,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontSize = 16.sp,
        fontFamily =bagelFamily,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontSize = 24.sp,
        fontFamily =cherryFamily,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
)