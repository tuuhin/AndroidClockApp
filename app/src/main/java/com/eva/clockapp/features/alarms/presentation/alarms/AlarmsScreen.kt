package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsScreenContent
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
	alarms: ImmutableList<AlarmsModel>,
	onEvent: (AlarmsScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	onCreateNewAlarm: () -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.alarms_screen_title)) },
				actions = {
					TextButton(onClick = onCreateNewAlarm) {
						Text(text = stringResource(R.string.create_action))
					}
				},
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		AlarmsScreenContent(
			alarms = alarms,
			onEnableAlarm = { isEnabled, alarm ->
				onEvent(AlarmsScreenEvents.OnEnableOrDisAbleAlarm(isEnabled, alarm))
			},
			onAlarmClick = {},
			onCreateNew = onCreateNewAlarm,
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
	AlarmScreen(alarms = AlarmPreviewFakes.FAKE_ALARMS_MODEL_LIST, onEvent = {})
}