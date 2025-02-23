package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.presentation.composables.RadioButtonWithTextItem
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
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
				.padding(paddingValues = scPadding)
				.padding(all = dimensionResource(R.dimen.sc_padding))
				.fillMaxWidth(),
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
			LazyColumn(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				itemsIndexed(items = VibrationPattern.entries) { _, pattern ->
					RadioButtonWithTextItem(
						text = pattern.toText,
						enabled = enabled,
						isSelected = pattern == selected,
						onClick = { onPatternChange(pattern) },
						colors = optionsColor,
						modifier = Modifier.fillMaxWidth()
					)
				}
			}
		}
	}
}

@Composable
fun AlarmVibrationScreen(
	state: AssociateAlarmFlags,
	onEvent: (AlarmFlagsChangeEvent) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) = AlarmVibrationScreen(
	selected = state.vibrationPattern,
	enabled = state.isVibrationEnabled,
	onEnableChange = { onEvent(AlarmFlagsChangeEvent.OnVibrationEnabled(it)) },
	onPatternChange = { onEvent(AlarmFlagsChangeEvent.OnVibrationPatternSelected(it)) },
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