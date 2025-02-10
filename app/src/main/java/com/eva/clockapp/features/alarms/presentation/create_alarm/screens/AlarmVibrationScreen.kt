package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.util.toText
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmVibrationScreen(
	selected: VibrationPattern? = VibrationPattern.SHORT,
	onPatternChange: (VibrationPattern) -> Unit = {},
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	onEnableChange: (Boolean) -> Unit = {},
	navigation: @Composable () -> Unit = {},
	optionsColor: RadioButtonColors = RadioButtonDefaults.colors(),
) {

	val layoutDirection = LocalLayoutDirection.current
	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.select_vibration_mode_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		Column(
			modifier = Modifier
				.padding(
					top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
					bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
					start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
					end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
				)
				.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			ListItem(
				headlineContent = { Text(text = stringResource(R.string.option_enabled)) },
				trailingContent = {
					Switch(
						checked = enabled,
						onCheckedChange = onEnableChange,
					)
				},
				colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
				modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
			)
			VibrationPattern.entries.forEach { pattern ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.clip(MaterialTheme.shapes.medium)
						.clickable(role = Role.RadioButton, enabled = enabled) {
							onPatternChange(pattern)
						}
				) {
					RadioButton(
						enabled = enabled,
						selected = pattern == selected,
						onClick = { onPatternChange(pattern) },
						colors = optionsColor
					)
					Text(
						text = pattern.toText,
						style = MaterialTheme.typography.bodyMedium,
						color = if (enabled) LocalContentColor.current
						else optionsColor.disabledUnselectedColor
					)
				}
			}
		}
	}
}

@Composable
fun AlarmVibrationScreen(
	state: AssociateAlarmFlags,
	onEvent: (CreateAlarmEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) = AlarmVibrationScreen(
	selected = state.vibrationPattern,
	enabled = state.isVibrationEnabled,
	onEnableChange = { onEvent(CreateAlarmEvents.OnVibrationEnabled(it)) },
	onPatternChange = { onEvent(CreateAlarmEvents.OnVibrationPatternSelected(it)) },
	modifier = modifier,
	navigation = navigation
)

@PreviewLightDark
@Composable
private fun AlarmVibrationPatternSelectorPreview() = ClockAppTheme {
	AlarmVibrationScreen(
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}