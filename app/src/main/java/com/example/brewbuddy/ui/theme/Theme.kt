// Theme.kt
package com.example.brewbuddy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = White,
    secondary = CoffeeOrange,
    onSecondary = Black,
    primaryContainer = CoffeeLightBrown,
    onPrimaryContainer = CoffeeDarkBrown,
    secondaryContainer = CoffeeLightOrange,
    onSecondaryContainer = CoffeeDarkOrange,
    surface = CoffeeCream,
    onSurface = CoffeeDarkBrown,
    surfaceVariant = CoffeeLightCream,
    onSurfaceVariant = CoffeeMediumBrown,
    background = CoffeeBackground,
    onBackground = CoffeeDarkBrown
)

private val DarkColorScheme = darkColorScheme(
    primary = CoffeeLightBrown,
    onPrimary = CoffeeDarkBrown,
    secondary = CoffeeLightOrange,
    onSecondary = CoffeeDarkOrange,
    primaryContainer = CoffeeBrown,
    onPrimaryContainer = White,
    secondaryContainer = CoffeeOrange,
    onSecondaryContainer = Black,
    surface = CoffeeMediumBrown,
    onSurface = White,
    surfaceVariant = CoffeeDarkBrown,
    onSurfaceVariant = CoffeeLightCream,
    background = CoffeeDarkBrown,
    onBackground = CoffeeLightCream
)

@Composable
fun BrewBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        typography = Typography, // Use your own or the default Typography
        content = content
    )
}
