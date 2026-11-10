package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val FoodHubDarkColorScheme = darkColorScheme(
    primary = FoodHubOrange,
    onPrimary = Color.White,
    primaryContainer = FoodHubOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = FoodHubGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF423500),
    onSecondaryContainer = FoodHubGoldLight,
    tertiary = VegGreen,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFECEFF1),
    surface = DarkSurface,
    onSurface = Color(0xFFECEFF1),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCFD8DC),
    outline = DarkOutline
)

private val FoodHubLightColorScheme = lightColorScheme(
    primary = FoodHubOrange,
    onPrimary = Color.White,
    primaryContainer = FoodHubOrangeLight,
    onPrimaryContainer = FoodHubOrangeDark,
    secondary = FoodHubGold,
    onSecondary = Color.Black,
    secondaryContainer = FoodHubGoldLight,
    onSecondaryContainer = Color(0xFF664400),
    tertiary = VegGreen,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF1F2937),
    surface = LightSurface,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = LightOutline
)

@Composable
fun FoodHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FoodHubDarkColorScheme
        else -> FoodHubLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FoodHubTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
