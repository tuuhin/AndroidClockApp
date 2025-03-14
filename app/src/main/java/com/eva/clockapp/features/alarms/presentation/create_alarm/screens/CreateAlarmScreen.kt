package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmScreen(
	state: CreateAlarmState,
	flags: AssociateAlarmFlags,
	onEvent: (CreateAlarmEvents) -> Unit,
	onFlagsEvent: (AlarmFlagsChangeEvent) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onNavigateVibrationScreen: () -> Unit = {},
	onNavigateSnoozeScreen: () -> Unit = {},
	onNavigateSoundScreen: () -> Unit = {},
	onNavigateBackgroundScreen: () -> Unit = {},
) {
	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = {
					Text(
						text = if (state.isAlarmCreate) stringResource(R.string.create_alarm_screen_title)
						else stringResource(R.string.update_alarm_screen_title)
					)
				},
				actions = {
					if (state.isAlarmCreate) {
						TextButton(onClick = { onEvent(CreateAlarmEvents.OnSaveAlarm) }) {
							Text(text = stringResource(R.string.save_action))
						}
					} else {
						TextButton(onClick = { onEvent(CreateAlarmEvents.OnUpdateAlarm) }) {
							Text(text = stringResource(R.string.update_action))
						}
					}
				},
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		CreateAlarmContent(
			state = state,
			flags = flags,
			onEvent = onEvent,
			onFlagsEvent = onFlagsEvent,
			onNavigateVibrationScreen = onNavigateVibrationScreen,
			onNavigateSnoozeScreen = onNavigateSnoozeScreen,
			onNavigateSoundScreen = onNavigateSoundScreen,
			onNavigateBackgroundScreen = onNavigateBackgroundScreen,
			contentPadding = PaddingValues(all = dimensionResource(R.dimen.sc_padding)),
			modifier = Modifier
				.fillMaxSize()
				.padding(scPadding),
		)
	}
}


@PreviewLightDark
@Composable
private fun AlarmScreenPreview() = ClockAppTheme {
	CreateAlarmScreen(
		state = AlarmPreviewFakes.FAKE_CREATE_ALARM_STATE,
		flags = AlarmPreviewFakes.FAKE_ASSOCIATE_FLAGS_STATE,
		onEvent = {},
		onFlagsEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}