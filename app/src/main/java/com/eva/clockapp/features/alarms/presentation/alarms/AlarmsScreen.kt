package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsScreenContent
import com.eva.clockapp.features.alarms.presentation.composables.AlarmsTopAppBar
import com.eva.clockapp.features.alarms.presentation.composables.DeleteAlarmsDialog
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
	alarms: ImmutableList<SelectableAlarmModel>,
	onEvent: (AlarmsScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	onAlarmClick: (AlarmsModel) -> Unit = {},
	onCreateNewAlarm: () -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	val isAnySelected by remember(alarms) {
		derivedStateOf { alarms.any { it.isSelected } }
	}
	var showDeleteDialog by remember { mutableStateOf(false) }

	BackHandler(
		enabled = isAnySelected,
		onBack = { onEvent(AlarmsScreenEvents.DeSelectAllAlarms) },
	)

	DeleteAlarmsDialog(
		showDialog = showDeleteDialog,
		onConfirm = { onEvent(AlarmsScreenEvents.DeleteSelectedAlarms) },
		onDismiss = { showDeleteDialog = false },
	)

	Scaffold(
		topBar = {
			AlarmsTopAppBar(
				selectableAlarms = alarms,
				onCreateNewAlarm = onCreateNewAlarm,
				scrollBehavior = scrollBehavior,
				navigation = navigation,
			)
		},
		floatingActionButton = {
			AnimatedVisibility(visible = isAnySelected) {
				ExtendedFloatingActionButton(
					text = { Text(text = stringResource(R.string.delete_action)) },
					icon = {
						Icon(
							imageVector = Icons.Outlined.Delete,
							contentDescription = stringResource(R.string.delete_action)
						)
					},
					shape = MaterialTheme.shapes.medium,
					onClick = { showDeleteDialog = true },
				)
			}
		},
		floatingActionButtonPosition = FabPosition.Center,
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		AlarmsScreenContent(
			alarms = alarms,
			onEnableAlarm = { enabled, alarm ->
				onEvent(AlarmsScreenEvents.OnEnableOrDisAbleAlarm(enabled, alarm))
			},
			onAlarmSelect = { alarm -> onEvent(AlarmsScreenEvents.ToggleAlarmSelection(alarm)) },
			onAlarmClick = onAlarmClick,
			onCreateNew = onCreateNewAlarm,
			contentPadding = PaddingValues(all = dimensionResource(R.dimen.sc_padding)),
			modifier = Modifier
				.fillMaxSize()
				.padding(scPadding),
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
	AlarmScreen(alarms = alarm, onEvent = {})
}