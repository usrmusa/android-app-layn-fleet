package com.digilayn.laynfleet.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Forest = Color(0xFF175C4C)
private val ForestDark = Color(0xFF0A352B)
private val Mint = Color(0xFFB9F4D8)
private val Sun = Color(0xFFFFD166)

private val LightColors = lightColorScheme(
    primary = Forest, onPrimary = Color.White, secondary = ForestDark, tertiary = Sun,
    background = Color(0xFFF5F8F6), surface = Color.White, onSurface = Color(0xFF17201D),
)
private val DarkColors = darkColorScheme(
    primary = Mint, onPrimary = ForestDark, secondary = Sun, tertiary = Sun,
    background = Color(0xFF09130F), surface = Color(0xFF10201B),
)
private val FleetTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
        fontSize = 38.sp, lineHeight = 43.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 34.sp,
    ),
)

@Composable
fun LaynFleetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FleetTypography,
        content = content,
    )
}
