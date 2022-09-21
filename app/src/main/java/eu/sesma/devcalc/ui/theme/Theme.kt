package eu.sesma.devcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.White,
    background = Color.DarkGray,
    surface = Color.LightGray,
    onBackground = Color.White,
    onSurface = Color.DarkGray,
)

private val LightColorPalette = lightColors(
    primary = Color.DarkGray,
    background = Color.White,
    surface = Color.LightGray,
    onBackground = Color.DarkGray,
    onSurface = Color.DarkGray,
)

@Composable
fun DevCalcTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}