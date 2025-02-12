package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
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
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmSoundOptions
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.toText
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
	labelState: String = "",
	isVibrationEnabled: Boolean = true,
	isSnoozeEnabled: Boolean = true,
	isSoundEnabled: Boolean = true,
	onTimeChange: (LocalTime) -> Unit,
	onWeekDaySelected: (DayOfWeek) -> Unit,
	modifier: Modifier = Modifier,
	onSnoozeEnabledChange: (Boolean) -> Unit = {},
	onVibrationEnabledChange: (Boolean) -> Unit = {},
	onLabelStateChange: (String) -> Unit,
	onSoundEnabledChange: (Boolean) -> Unit = {},
	onSelectSnoozeOption: () -> Unit = {},
	onSelectVibrationOption: () -> Unit = {},
	onSelectAlarmSound: () -> Unit = {},
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
			ScrollableTimePicker(onTimeSelected = onTimeChange)
		}
		item {
			WeekDayPicker(
				selectedDays = selectedDays,
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
				keyboardActions = KeyboardActions(onDone = { onLabelStateChange(labelState) }),
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Done,
					keyboardType = KeyboardType.Text,
				),
				modifier = Modifier
					.fillMaxWidth()
					.imeNestedScroll(),
				shape = MaterialTheme.shapes.small,
				leadingIcon = {
					Icon(
						imageVector = Icons.Outlined.Check,
						contentDescription = null
					)
				},
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = Color.Transparent,
					focusedContainerColor = Color.Transparent,
					unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
					focusedIndicatorColor = MaterialTheme.colorScheme.outline
				)
			)
		}
		item {
			Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
			}
		}
	}
}

@Composable
fun CreateAlarmContent(
	state: CreateAlarmState,
	flags: AssociateAlarmFlags,
	soundOptions: AlarmSoundOptions,
	onEvent: (CreateAlarmEvents) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	onNavigateVibrationScreen: () -> Unit = {},
	onNavigateSnoozeScreen: () -> Unit = {},
	onNavigateSoundScreen: () -> Unit = {},
) {
	CreateAlarmContent(
		selectedDays = state.selectedDays,
		labelState = state.labelState,
		repeatMode = flags.snoozeRepeatMode,
		vibrationPattern = flags.vibrationPattern,
		isVibrationEnabled = flags.isVibrationEnabled,
		isSoundEnabled = flags.isSoundEnabled,
		isSnoozeEnabled = flags.isSnoozeEnabled,
		snoozeInterval = flags.snoozeInterval,
		selectedRingtone = soundOptions.selectedSound,
		onWeekDaySelected = { onEvent(CreateAlarmEvents.OnAddOrRemoveWeekDay(it)) },
		onTimeChange = { onEvent(CreateAlarmEvents.OnAlarmTimeSelected(it)) },
		onSnoozeEnabledChange = { onEvent(CreateAlarmEvents.OnSnoozeEnabled(it)) },
		onVibrationEnabledChange = { onEvent(CreateAlarmEvents.OnVibrationEnabled(it)) },
		onLabelStateChange = { onEvent(CreateAlarmEvents.OnLabelValueChange(it)) },
		onSelectVibrationOption = onNavigateVibrationScreen,
		onSelectSnoozeOption = onNavigateSnoozeScreen,
		onSelectAlarmSound = onNavigateSoundScreen,
		contentPadding = contentPadding,
		modifier = modifier,
	)
}