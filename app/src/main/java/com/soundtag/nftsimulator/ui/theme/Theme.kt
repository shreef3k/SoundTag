package com.soundtag.nftsimulator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TelegramScheme = lightColorScheme(
    primary = Color(0xFF2AABEE),
    secondary = Color(0xFF54A9EB),
    surface = Color.White,
    background = Color(0xFFF4F7FA),
)

@Composable
fun NftSimulatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TelegramScheme, content = content)
}
