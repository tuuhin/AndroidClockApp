package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.presentation.util.toText
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun SnoozeRepeatModePicker(
	repeat: SnoozeRepeatMode,
	onRepeatModeChange: (SnoozeRepeatMode) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	optionsColor: RadioButtonColors = RadioButtonDefaults.colors(),
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp),
	) {
		ListItem(
			headlineContent = { Text(text = stringResource(R.string.snooze_repeat_options_title)) },
			colors = ListItemDefaults.colors(
				containerColor = Color.Transparent,
				headlineColor = if (enabled) LocalContentColor.current else MaterialTheme.colorScheme.onSurfaceVariant
			)
		)
		SnoozeRepeatMode.entries.forEach { option ->
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.clip(MaterialTheme.shapes.medium)
					.clickable(role = Role.RadioButton) { onRepeatModeChange(option) }
			) {
				RadioButton(
					selected = option == repeat,
					onClick = { onRepeatModeChange(option) },
					colors = optionsColor,
					enabled = enabled
				)
				Text(
					text = option.toText,
					style = MaterialTheme.typography.bodyMedium,
					color = if (enabled) LocalContentColor.current else optionsColor.disabledUnselectedColor
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun SnoozeRepeatModePickerPreview() = ClockAppTheme {
	Surface {
		SnoozeRepeatModePicker(
			repeat = SnoozeRepeatMode.THREE,
			onRepeatModeChange = {},
		)
	}
}