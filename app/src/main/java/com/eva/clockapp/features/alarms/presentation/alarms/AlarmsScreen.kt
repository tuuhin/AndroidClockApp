package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.alarms.state.AlarmsScreenEvents
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsBottomBar
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsScreenContent
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsTopAppBar
import com.eva.clockapp.features.alarms.presentation.composables.DeleteAlarmsDialog
import com.eva.clockapp.features.alarms.presentation.composables.HasNotificationPermission
import com.eva.clockapp.features.alarms.presentation.composables.HasScheduleAlarmPermissionsDialog
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
	isContentReady: Boolean,
	selectableAlarms: ImmutableList<SelectableAlarmModel>,
	onEvent: (AlarmsScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	nextAlarmScheduled: Duration? = null,
	onSelectAlarm: (AlarmsModel) -> Unit = {},
	onCreateNewAlarm: () -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	val isAnySelected by remember(selectableAlarms) {
		derivedStateOf { selectableAlarms.any { it.isSelected } }
	}

	var showDeleteDialog by remember { mutableStateOf(false) }

	BackHandler(
		enabled = isAnySelected,
		onBack = { onEvent(AlarmsScreenEvents.DeSelectAllAlarms) },
	)

	DeleteAlarmsDialog(
		showDialog = showDeleteDialog,
		onConfirm = {
			onEvent(AlarmsScreenEvents.DeleteSelectedAlarms)
			showDeleteDialog = false
		},
		onDismiss = { showDeleteDialog = false },
	)

	// check schedule exact alarm permission
	HasScheduleAlarmPermissionsDialog()

	// check notification permission
	HasNotificationPermission()

	Scaffold(
		topBar = {
			AlarmsTopAppBar(
				selectableAlarms = selectableAlarms,
				onCreateNewAlarm = onCreateNewAlarm,
				onSelectAll = { onEvent(AlarmsScreenEvents.OnSelectAllAlarms) },
				scrollBehavior = scrollBehavior,
				navigation = navigation,
			)
		},
		bottomBar = {
			AlarmsBottomBar(
				selectableAlarms = selectableAlarms,
				showBottomBar = isAnySelected,
				onDelete = { showDeleteDialog = true },
				onEnableAlarms = { onEvent(AlarmsScreenEvents.OnEnableSelectedAlarms) },
				onDisableAlarms = { onEvent(AlarmsScreenEvents.OnDisableSelectedAlarms) }
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		AlarmsScreenContent(
			isLoaded = isContentReady,
			alarms = selectableAlarms,
			nextAlarmSchedule = nextAlarmScheduled,
			onEnableAlarm = { _, alarm ->
				onEvent(AlarmsScreenEvents.OnEnableOrDisAbleAlarm(alarm))
			},
			onAlarmSelect = { alarm ->
				onEvent(AlarmsScreenEvents.OnSelectOrUnSelectAlarm(alarm))
			},
			onSelectAlarm = onSelectAlarm,
			contentPadding = PaddingValues(all = dimensionResource(R.dimen.sc_padding)),
			modifier = Modifier
				.fillMaxSize()
				.padding(scPadding)
		)
	}
}

private class AlarmsListPreviewParams :
	CollectionPreviewParameterProvider<ImmutableList<SelectableAlarmModel>>(
		listOf(
			AlarmPreviewFakes.FAKE_ALARMS_MODEL_LIST_EMPTY,
			AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL_LIST,
			AlarmPreviewFakes.FAKE_SELECTABLE_ALARMS_LIST_SELECTED,
		)
	)


@PreviewLightDark
@Composable
private fun AlarmScreenPreview(
	@PreviewParameter(AlarmsListPreviewParams::class)
	alarm: ImmutableList<SelectableAlarmModel>,
) = ClockAppTheme {
	AlarmScreen(
		isContentReady = true,
		selectableAlarms = alarm,
		nextAlarmScheduled = 4.days,
		onEvent = {},
	)
}