package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.core.utils.checkMusicReadPermission
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.presentation.composables.CheckReadMusicPermission
import com.eva.clockapp.features.alarms.presentation.composables.ConfigureAlarmSoundSheet
import com.eva.clockapp.features.alarms.presentation.composables.RadioButtonWithTextItem
import com.eva.clockapp.features.alarms.presentation.create_alarm.CategoricalRingtones
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.features.alarms.presentation.util.toText
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AlarmSoundScreen(
	flags: AssociateAlarmFlags,
	selectedOption: RingtoneMusicFile,
	ringtones: CategoricalRingtones,
	onItemSelected: (RingtoneMusicFile) -> Unit,
	onVolumeChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	onIncreaseVolumeByStep: (Boolean) -> Unit = {},
	onLoadExternalRingtones: () -> Unit = {},
	onEnableChange: (Boolean) -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val context = LocalContext.current
	val snackBarHostState = LocalSnackBarHostState.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	var hasPermission by remember { mutableStateOf(context.checkMusicReadPermission) }
	var showBottomSheet by remember { mutableStateOf(false) }

	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()

	if (showBottomSheet) {
		ModalBottomSheet(
			sheetState = sheetState,
			onDismissRequest = { showBottomSheet = false },
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			contentColor = MaterialTheme.colorScheme.onSurface,
		) {
			ConfigureAlarmSoundSheet(
				isVolumeStepIncrease = flags.isVolumeStepIncrease,
				volume = flags.alarmVolume,
				onVolumeStepIncreaseChange = onIncreaseVolumeByStep,
				onVolumeChange = onVolumeChange,
			)
		}
	}

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.select_alarm_sound_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					TextButton(
						onClick = {
							scope.launch { sheetState.show() }
								.invokeOnCompletion { showBottomSheet = true }
						},
					) {
						Text(text = stringResource(R.string.alarm_sound_configure))
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		Column(
			modifier = Modifier
				.padding(paddingValues = scPadding)
				.padding(all = dimensionResource(R.dimen.sc_padding))
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			ListItem(
				headlineContent = { Text(text = stringResource(R.string.option_enabled)) },
				trailingContent = {
					Switch(checked = enabled, onCheckedChange = onEnableChange)
				},
				colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
				modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
			)
			LazyColumn(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ringtones.forEach { (key, ringtones) ->
					stickyHeader {
						ListItem(
							headlineContent = { Text(text = key.toText) },
							colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
						)
					}
					itemsIndexed(items = ringtones) { _, item ->
						RadioButtonWithTextItem(
							text = item.name,
							isSelected = item == selectedOption,
							onClick = { onItemSelected(item) },
							modifier = Modifier.fillMaxWidth()
						)
					}
					val isNoRingtones = ringtones.isEmpty() && hasPermission
					if (key == RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL && isNoRingtones) {
						item {
							Column(
								modifier = Modifier
									.fillMaxWidth()
									.heightIn(min = 240.dp)
									.animateItem(),
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.Center
							) {
								Icon(
									painter = painterResource(R.drawable.ic_music_notes),
									contentDescription = null,
									tint = MaterialTheme.colorScheme.secondary,
									modifier = Modifier.size(72.dp)
								)
								Spacer(modifier = Modifier.height(20.dp))
								Text(
									text = stringResource(R.string.no_music_files_type_alarm),
									style = MaterialTheme.typography.bodyMedium
								)
							}
						}
					}
				}
				if (!hasPermission) {
					item {
						CheckReadMusicPermission(
							onPermissionChanged = { isAllowed ->
								hasPermission = isAllowed
								onLoadExternalRingtones()
							},
							modifier = Modifier.animateItem()
						)
					}
				}
			}
		}
	}
}

@Composable
fun AlarmSoundScreen(
	state: CreateAlarmState,
	flags: AssociateAlarmFlags,
	ringtones: CategoricalRingtones,
	onEvent: (CreateAlarmEvents) -> Unit,
	onFlagsEvent: (AlarmFlagsChangeEvent) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {
	LifecycleStartEffect(Unit) {
		onStopOrDispose {
			// called on-stop or disposed
			onEvent(CreateAlarmEvents.OnExitAlarmSoundScreen)
		}
	}

	AlarmSoundScreen(
		flags = flags,
		selectedOption = state.ringtone,
		ringtones = ringtones,
		enabled = flags.isSoundEnabled,
		onItemSelected = { onFlagsEvent(AlarmFlagsChangeEvent.OnSoundSelected(it)) },
		onEnableChange = { onFlagsEvent(AlarmFlagsChangeEvent.OnSoundOptionEnabled(it)) },
		onLoadExternalRingtones = { onEvent(CreateAlarmEvents.LoadDeviceRingtoneFiles) },
		onIncreaseVolumeByStep = { onFlagsEvent(AlarmFlagsChangeEvent.OnIncreaseVolumeByStep(it)) },
		onVolumeChange = { onFlagsEvent(AlarmFlagsChangeEvent.OnSoundVolumeChange(it)) },
		modifier = modifier,
		navigation = navigation,
	)
}


@PreviewLightDark
@Composable
private fun AlarmsSoundsScreenPreview() = ClockAppTheme {
	AlarmSoundScreen(
		flags = AlarmPreviewFakes.FAKE_ASSOCIATE_FLAGS_STATE,
		state = AlarmPreviewFakes.FAKE_CREATE_ALARM_STATE,
		ringtones = AlarmPreviewFakes.FAKE_RINGTONES_OPTIONS,
		onFlagsEvent = {},
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}
