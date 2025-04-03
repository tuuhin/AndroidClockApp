package com.eva.clockapp.features.alarms.presentation.play_alarm

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.eva.clockapp.features.alarms.presentation.play_alarm.composables.AlarmsBackground
import com.eva.clockapp.features.alarms.presentation.play_alarm.composables.PlayAlarmScreenContent
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun PlayAlarmsScreen(
	dateTime: LocalDateTime,
	modifier: Modifier = Modifier,
	isPreview: Boolean = false,
	is24HrFormat: Boolean = false,
	onStopAlarm: () -> Unit = {},
	onSnoozeAlarm: () -> Unit = {},
	onPreview: () -> Unit = {},
	backgroundImage: String? = null,
	labelText: String? = null,
	borderStroke: BorderStroke? = null,
	shape: Shape = RectangleShape,
) {
	Surface(
		modifier = modifier,
		shape = shape,
		border = borderStroke,
	) {
		AlarmsBackground(
			backgroundImage = backgroundImage,
			isPreview = isPreview,
			containerColor = MaterialTheme.colorScheme.background,
		)
		PlayAlarmScreenContent(
			dateTime = dateTime,
			onSnoozeAlarm = onSnoozeAlarm,
			onStopAlarm = onStopAlarm,
			labelText = labelText,
			isPreview = isPreview,
			onPreview = onPreview,
			is24HrFormat = is24HrFormat,
			textColorPrimary = if (backgroundImage != null) Color.White else MaterialTheme.colorScheme.onSurface,
			textColorSecondary = if (backgroundImage != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@PreviewLightDark
@Composable
private fun PlayAlarmsScreenPreview() = ClockAppTheme {
	PlayAlarmsScreen(
		dateTime = LocalDateTime(2025, 3, 4, 12, 0),
		labelText = AlarmPreviewFakes.LOREM_TEXT,
		onStopAlarm = {},
		onSnoozeAlarm = {},
	)
}