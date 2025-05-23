package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.presentation.composables.ScrollableTimePicker
import com.eva.clockapp.features.alarms.presentation.composables.WeekDayPicker
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmNavEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.toText
import com.eva.clockapp.features.settings.data.utils.is24HrFormat
import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateAlarmContent(
	selectedDays: ImmutableSet<DayOfWeek>,
	selectedRingtone: RingtoneMusicFile,
	vibrationPattern: VibrationPattern,
	snoozeInterval: SnoozeIntervalOption,
	repeatMode: SnoozeRepeatMode,
	onTimeChange: (LocalTime) -> Unit,
	onWeekDaySelected: (DayOfWeek) -> Unit,
	modifier: Modifier = Modifier,
	timePickerStartTime: LocalTime = LocalTime(0, 0),
	labelState: String = "",
	isVibrationEnabled: Boolean = true,
	isSnoozeEnabled: Boolean = true,
	isSoundEnabled: Boolean = true,
	timeFormat: TimeFormatOptions = TimeFormatOptions.SYSTEM_DEFAULT,
	startOfWeek: StartOfWeekOptions = StartOfWeekOptions.SYSTEM_DEFAULT,
	onSnoozeEnabledChange: (Boolean) -> Unit = {},
	onVibrationEnabledChange: (Boolean) -> Unit = {},
	onLabelStateChange: (String) -> Unit,
	onSoundEnabledChange: (Boolean) -> Unit = {},
	onSelectSnoozeOption: () -> Unit = {},
	onSelectVibrationOption: () -> Unit = {},
	onSelectAlarmSound: () -> Unit = {},
	onSelectAlarmBackground: () -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(),
	optionsColors: ListItemColors = ListItemDefaults.colors(containerColor = Color.Transparent),
) {

	LazyColumn(
		modifier = modifier,
		contentPadding = contentPadding,
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		item {
			ScrollableTimePicker(
				startTime = timePickerStartTime,
				is24HrFormat = timeFormat.is24HrFormat,
				onTimeSelected = onTimeChange,
				fontFamily = DownloadableFonts.FUNNEL_DISPLAY,
			)
		}
		item {
			WeekDayPicker(
				selectedDays = selectedDays,
				startOfWeek = startOfWeek,
				onSelectDay = onWeekDaySelected,
				modifier = Modifier.fillMaxWidth()
			)
		}
		item {
			TextField(
				value = labelState,
				onValueChange = onLabelStateChange,
				placeholder = { Text(text = stringResource(R.string.create_alarm_label_placeholder)) },
				singleLine = true,
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Done,
					keyboardType = KeyboardType.Text,
				),
				prefix = {
					Icon(
						imageVector = Icons.Outlined.TextFields,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.secondary,
					)
				},
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = Color.Transparent,
					focusedContainerColor = Color.Transparent,
					unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
					focusedIndicatorColor = MaterialTheme.colorScheme.outline
				),
				shape = MaterialTheme.shapes.small,
				modifier = Modifier
					.fillMaxWidth()
					.imeNestedScroll(),
			)
		}
		item {
			Column(
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				ListItem(
					headlineContent = { Text(text = stringResource(R.string.alarm_sound_title)) },
					supportingContent = { Text(selectedRingtone.name) },
					trailingContent = {
						Switch(
							checked = isSoundEnabled,
							onCheckedChange = onSoundEnabledChange
						)
					},
					colors = optionsColors,
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.clickable(role = Role.Button, onClick = onSelectAlarmSound)
				)
				ListItem(
					headlineContent = { Text(text = stringResource(R.string.snooze_interval_title)) },
					supportingContent = { Text(text = "${snoozeInterval.toText} ${repeatMode.toText}") },
					trailingContent = {
						Switch(
							checked = isSnoozeEnabled,
							onCheckedChange = onSnoozeEnabledChange
						)
					},
					colors = optionsColors,
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.clickable(role = Role.Button, onClick = onSelectSnoozeOption)
				)
				ListItem(
					headlineContent = { Text(text = stringResource(R.string.vibration_pattern_title)) },
					supportingContent = { Text(text = vibrationPattern.toText) },
					trailingContent = {
						Switch(
							checked = isVibrationEnabled,
							onCheckedChange = onVibrationEnabledChange,
						)
					},
					colors = optionsColors,
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.clickable(role = Role.Button, onClick = onSelectVibrationOption)
				)
				ListItem(
					headlineContent = { Text(text = stringResource(R.string.select_alarms_background_title)) },
					supportingContent = { Text(text = stringResource(R.string.select_alarms_background_desc)) },
					colors = optionsColors,
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.clickable(role = Role.Button, onClick = onSelectAlarmBackground)
				)
			}
		}
	}
}

@Composable
fun CreateAlarmContent(
	state: CreateAlarmState,
	flags: AssociateAlarmFlags,
	onEvent: (CreateAlarmEvents) -> Unit,
	onFlagsEvent: (AlarmFlagsChangeEvent) -> Unit,
	onNavigationEvent: (CreateAlarmNavEvent) -> Unit,
	modifier: Modifier = Modifier,
	settings: AlarmSettingsModel = AlarmSettingsModel(),
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {

	CreateAlarmContent(
		timePickerStartTime = state.selectedTime,
		selectedDays = state.selectedDays,
		labelState = state.labelState,
		repeatMode = flags.snoozeRepeatMode,
		selectedRingtone = state.ringtone,
		vibrationPattern = flags.vibrationPattern,
		isVibrationEnabled = flags.isVibrationEnabled,
		isSoundEnabled = flags.isSoundEnabled,
		isSnoozeEnabled = flags.isSnoozeEnabled,
		snoozeInterval = flags.snoozeInterval,
		startOfWeek = settings.startOfWeek,
		timeFormat = settings.timeFormat,
		onWeekDaySelected = { onEvent(CreateAlarmEvents.OnAddOrRemoveWeekDay(it)) },
		onTimeChange = { onEvent(CreateAlarmEvents.OnAlarmTimeChange(it)) },
		onLabelStateChange = { onEvent(CreateAlarmEvents.OnLabelValueChange(it)) },
		onSnoozeEnabledChange = { onFlagsEvent(AlarmFlagsChangeEvent.OnSnoozeEnabled(it)) },
		onVibrationEnabledChange = { onFlagsEvent(AlarmFlagsChangeEvent.OnVibrationEnabled(it)) },
		onSoundEnabledChange = { onFlagsEvent(AlarmFlagsChangeEvent.OnSoundOptionEnabled(it)) },
		onSelectVibrationOption = { onNavigationEvent(CreateAlarmNavEvent.NavigateToVibrationScreen) },
		onSelectSnoozeOption = { onNavigationEvent(CreateAlarmNavEvent.NavigateToSnoozeScreen) },
		onSelectAlarmSound = { onNavigationEvent(CreateAlarmNavEvent.NavigateToSoundScreen) },
		onSelectAlarmBackground = { onNavigationEvent(CreateAlarmNavEvent.NavigateToBackgroundScreen) },
		contentPadding = contentPadding,
		modifier = modifier,
	)
}