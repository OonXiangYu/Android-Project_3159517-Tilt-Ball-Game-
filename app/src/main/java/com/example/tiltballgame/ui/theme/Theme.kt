package com.example.tiltballgame.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Blue40,
    onPrimary = White,
    secondary = Red40,
    onSecondary = Yellow,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
)

private val DarkColors = darkColorScheme(
    primary = Blue40,
    onPrimary = White,
    secondary = Red40,
    onSecondary = Yellow,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
)

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun TiltBallGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}