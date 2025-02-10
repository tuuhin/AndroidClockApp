package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmScreen(
	state: CreateAlarmState,
	onEvent: (CreateAlarmEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onNavigateVibrationScreen: () -> Unit = {},
	onNavigateSnoozeScreen: () -> Unit = {},
) {
	val layoutDirection = LocalLayoutDirection.current
	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = stringResource(R.string.create_alarm_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				text = { Text(text = stringResource(R.string.save_action)) },
				icon = {
					Icon(
						imageVector = Icons.Default.Alarm,
						contentDescription = stringResource(R.string.save_action)
					)
				},
				onClick = { onEvent(CreateAlarmEvents.OnSaveAlarm) },
				shape = MaterialTheme.shapes.large
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		CreateAlarmContent(
			selectedDays = state.selectedDays,
			vibrationPattern = state.flags.vibrationPattern,
			isVibrationEnabled = state.flags.isVibrationEnabled,
			isSoundEnabled = state.flags.isSoundEnabled,
			isSnoozeEnabled = state.flags.isSnoozeEnabled,
			snoozeInterval = state.flags.snoozeInterval,
			labelState = state.labelState,
			repeatMode = state.flags.snoozeRepeatMode,
			onWeekDaySelected = { onEvent(CreateAlarmEvents.OnAddOrRemoveWeekDay(it)) },
			onTimeChange = { onEvent(CreateAlarmEvents.OnAlarmTimeSelected(it)) },
			onSnoozeEnabledChange = { onEvent(CreateAlarmEvents.OnSnoozeEnabled(it)) },
			onVibrationEnabledChange = { onEvent(CreateAlarmEvents.OnVibrationEnabled(it)) },
			onSelectVibrationOption = onNavigateVibrationScreen,
			onSelectSnoozeOption = onNavigateSnoozeScreen,
			onLabelStateChange = { onEvent(CreateAlarmEvents.OnLabelValueChange(it)) },
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier.fillMaxSize(),
		)
	}
}


@PreviewLightDark
@Composable
private fun AlarmScreenPreview() = ClockAppTheme {
	CreateAlarmScreen(
		state = CreateAlarmState(),
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}