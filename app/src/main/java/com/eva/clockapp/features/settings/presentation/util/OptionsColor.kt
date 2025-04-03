package com.eva.clockapp.features.settings.presentation.util

import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ListItemDefaults.settingsOptionColor(): ListItemColors = colors(
	containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
	headlineColor = MaterialTheme.colorScheme.onSurface,
	supportingColor = MaterialTheme.colorScheme.secondary
)