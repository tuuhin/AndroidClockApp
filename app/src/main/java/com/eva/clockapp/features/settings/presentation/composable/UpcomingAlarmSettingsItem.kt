package com.eva.clockapp.features.settings.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption
import com.eva.clockapp.features.settings.presentation.util.settingsOptionColor
import com.eva.clockapp.features.settings.presentation.util.toText

@Composable
fun UpcomingAlarmSettingsItem(
	option: UpcomingAlarmTimeOption,
	onSelectOption: (UpcomingAlarmTimeOption) -> Unit,
	modifier: Modifier = Modifier,
	leading: (@Composable () -> Unit)? = null,
	enabled: Boolean = true,
	shadowElevation: Dp = 0.dp,
	tonalElevation: Dp = 0.dp,
	colors: ListItemColors = ListItemDefaults.settingsOptionColor(),
) {

	var showDropDown by remember { mutableStateOf(false) }

	ListItem(
		headlineContent = {
			Text(
				text = stringResource(R.string.settings_upcoming_alarm_notification_time_title),
				style = MaterialTheme.typography.titleMedium,
				color = if (enabled) MaterialTheme.colorScheme.onBackground
				else MaterialTheme.colorScheme.onSurfaceVariant
			)
		},
		supportingContent = {
			Text(
				text = option.toText,
				style = MaterialTheme.typography.labelMedium,
				color = if (enabled) MaterialTheme.colorScheme.onBackground
				else MaterialTheme.colorScheme.onSurfaceVariant
			)
		},
		trailingContent = {
			Box {
				IconButton(onClick = { showDropDown = true }) {
					Icon(
						imageVector = Icons.Default.MoreVert,
						contentDescription = "More options"
					)
				}
				DropdownMenu(
					expanded = showDropDown,
					onDismissRequest = { showDropDown = false },
					shape = MaterialTheme.shapes.large
				) {
					UpcomingAlarmTimeOption.entries.forEach { settingsOption ->
						DropdownMenuItem(
							text = { Text(settingsOption.toText) },
							onClick = { onSelectOption(settingsOption) },
							colors = if (settingsOption == option) MenuDefaults.itemColors(
								textColor = MaterialTheme.colorScheme.primary
							)
							else MenuDefaults.itemColors()
						)
					}
				}
			}
		},
		leadingContent = leading,
		tonalElevation = tonalElevation,
		shadowElevation = shadowElevation,
		colors = colors,
		modifier = modifier.clip(shape = MaterialTheme.shapes.medium),
	)
}
