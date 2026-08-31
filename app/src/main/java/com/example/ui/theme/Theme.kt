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

private val DarkColorScheme = darkColorScheme(
    primary = IzmirBlueLight,
    onPrimary = Slate950,
    primaryContainer = IzmirNavyLight,
    onPrimaryContainer = IzmirBlueContainer,
    secondary = GedizYellow,
    onSecondary = Slate950,
    secondaryContainer = GedizAmberOnContainer,
    onSecondaryContainer = GedizAmberContainer,
    tertiary = StatusGreen,
    background = Slate950,
    surface = Slate900,
    surfaceVariant = Slate800,
    onBackground = Slate50,
    onSurface = Slate50,
    onSurfaceVariant = Slate300,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = IzmirBlue,
    onPrimary = Color.White,
    primaryContainer = IzmirBlueContainer,
    onPrimaryContainer = IzmirBlueOnContainer,
    secondary = GedizAmber,
    onSecondary = Color.White,
    secondaryContainer = GedizAmberContainer,
    onSecondaryContainer = GedizAmberOnContainer,
    tertiary = StatusGreen,
    background = Slate50,
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate600,
    outline = Slate300
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our custom cohesive brand scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
