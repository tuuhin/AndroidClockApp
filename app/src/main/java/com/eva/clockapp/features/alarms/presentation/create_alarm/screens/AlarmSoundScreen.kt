package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSoundScreen(
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {
	val layoutDirection = LocalLayoutDirection.current
	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.select_alarm_sound_screen_title)) },
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
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {

		}
	}
}


@PreviewLightDark
@Composable
private fun AlarmsSoundsScreenPreview() = ClockAppTheme {
	AlarmSoundScreen()
}
