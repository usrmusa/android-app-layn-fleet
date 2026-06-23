package com.digilayn.laynfleet.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LaynGreen = Color(0xFF9CD329)
val LaynCharcoal = Color(0xFF333333)
private val LaynGreenLight = Color(0xFFB8F044)
private val LaynGreenContainer = Color(0xFFE8FFC0)
private val LaynCharcoalContainer = Color(0xFFE5E2E1)
private val LaynBackground = Color(0xFFFAFAF6)
private val LaynDarkBackground = Color(0xFF11130E)
private val LaynDarkSurface = Color(0xFF1B1D18)

private val LightColors = lightColorScheme(
    primary = LaynGreen,
    onPrimary = LaynCharcoal,
    primaryContainer = LaynGreenContainer,
    onPrimaryContainer = Color(0xFF152000),
    secondary = LaynCharcoal,
    onSecondary = Color.White,
    secondaryContainer = LaynCharcoalContainer,
    onSecondaryContainer = Color(0xFF1C1B1B),
    tertiary = LaynGreen,
    onTertiary = Color(0xFF1D2C00),
    tertiaryContainer = LaynGreenContainer,
    onTertiaryContainer = Color(0xFF152000),
    background = LaynBackground,
    onBackground = LaynCharcoal,
    surface = Color.White,
    onSurface = LaynCharcoal,
    surfaceVariant = Color(0xFFE5E4DD),
    onSurfaceVariant = Color(0xFF474842),
    outline = Color(0xFF777870),
)

private val DarkColors = darkColorScheme(
    primary = LaynGreenLight,
    onPrimary = Color(0xFF1D2C00),
    primaryContainer = Color(0xFF2E4400),
    onPrimaryContainer = LaynGreenContainer,
    secondary = LaynCharcoal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF484646),
    onSecondaryContainer = Color(0xFFE5E2E1),
    tertiary = LaynGreen,
    onTertiary = Color(0xFF1D2C00),
    tertiaryContainer = Color(0xFF2E4400),
    onTertiaryContainer = LaynGreenContainer,
    background = LaynDarkBackground,
    onBackground = Color(0xFFE4E4DC),
    surface = LaynDarkSurface,
    onSurface = Color(0xFFE4E4DC),
    surfaceVariant = Color(0xFF454740),
    onSurfaceVariant = Color(0xFFC6C8BE),
    outline = Color(0xFF909289),
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

private val FleetShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
)

@Composable
fun LaynFleetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FleetTypography,
        shapes = FleetShapes,
        content = content,
    )
}
