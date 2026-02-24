package edu.nd.pmcburne.hwapp.one.ui.theme

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
    primary = UVAOrange,
    secondary = MutedBlue,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E6E6),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E6E6),

    surfaceVariant = Color(0xFF2C2F36), // Dark card
    onSurfaceVariant = Color(0xFFC6C9D1),

    outline = Color(0xFF8A8D93),

    error = Color(0xFFF2B8B5),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = UVAOrange,
    secondary = UVANavy,

    background = Color(0xFFF8F9FB),     // Very light gray
    onBackground = Color(0xFF1C1B1F),

    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = Color(0xFFE6E8EC), // Card background
    onSurfaceVariant = Color(0xFF44474F),

    outline = Color(0xFFB0B3BA),

    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun HWStarterRepoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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