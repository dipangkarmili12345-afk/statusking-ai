package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.statusking.ai.ui.theme.StatusKingTheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    StatusKingTheme(
        themeMode = if (darkTheme) "DARK" else "LIGHT",
        content = content
    )
}
