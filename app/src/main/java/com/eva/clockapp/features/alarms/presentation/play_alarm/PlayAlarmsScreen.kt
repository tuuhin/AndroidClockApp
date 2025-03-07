package com.eva.clockapp.features.alarms.presentation.play_alarm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.WEEK_MONTH_DAY
import com.eva.clockapp.ui.theme.ClockAppTheme
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

@Composable
fun PlayAlarmsScreen(
	dateTime: LocalDateTime,
	onStopAlarm: () -> Unit,
	onSnoozeAlarm: () -> Unit,
	modifier: Modifier = Modifier,
	labelText: String? = null,
) {
	Scaffold(modifier = modifier) { scPadding ->
		Box(
			modifier = Modifier
				.padding(scPadding)
				.padding(all = dimensionResource(R.dimen.alarms_screen_padding))
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = dateTime.time.format(LocalTime.Formats.HH_MM),
					style = MaterialTheme.typography.displayLarge,
					fontFamily = DownloadableFonts.BUNGEE
				)
				Text(
					text = dateTime.date.format(LocalDate.Formats.WEEK_MONTH_DAY),
					style = MaterialTheme.typography.headlineSmall
				)
				Text(
					text = labelText ?: "Alarm",
					style = MaterialTheme.typography.headlineSmall,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					textAlign = TextAlign.Center,
					modifier = Modifier.widthIn(max = 140.dp)
				)
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.offset(y = (-20).dp)
			) {
				FloatingActionButton(onClick = onStopAlarm, shape = CircleShape) {
					Icon(imageVector = Icons.Default.Close, contentDescription = null)
				}
				OutlinedButton(
					onClick = onSnoozeAlarm,
					border = BorderStroke(
						color = MaterialTheme.colorScheme.onBackground,
						width = 2.dp
					),
					shape = MaterialTheme.shapes.extraLarge,
					colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
					modifier = Modifier.widthIn(max = 120.dp),
				) {
					Text(text = stringResource(R.string.snooze_options_title))
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun PlayAlarmsScreenPreview() = ClockAppTheme {
	PlayAlarmsScreen(
		dateTime = LocalDateTime(2025, 3, 4, 12, 0),
		onStopAlarm = {},
		onSnoozeAlarm = {},
	)
}