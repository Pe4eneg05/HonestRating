package com.pechenegmobilecompanyltd.honestrating.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLightColor,
    onPrimaryContainer = OnPrimary,

    secondary = SecondaryColor,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLightColor,
    onSecondaryContainer = OnSecondary,

    tertiary = TertiaryColor,
    onTertiary = OnPrimary,
    tertiaryContainer = TertiaryLightColor,
    onTertiaryContainer = OnPrimary,

    background = BackgroundColor,
    onBackground = OnBackground,

    surface = SurfaceColor,
    onSurface = OnSurface,

    error = ErrorColor,
    onError = OnError,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// Определяем систему форм для компонентов
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // Для маленьких элементов (чипы, кнопки меню)
    small = RoundedCornerShape(8.dp),       // Для кнопок, полей ввода
    medium = RoundedCornerShape(12.dp),     // Для карточек, диалогов
    large = RoundedCornerShape(16.dp),      // Для больших карточек
    extraLarge = RoundedCornerShape(28.dp)  // Для FAB, полноэкранных диалогов
)

@Composable
fun HonestRatingTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        // Позже добавим темную тему
        LightColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}