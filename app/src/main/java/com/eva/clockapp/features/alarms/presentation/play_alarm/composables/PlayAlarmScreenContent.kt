package com.eva.clockapp.features.alarms.presentation.play_alarm.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.WEEK_MONTH_DAY
import com.eva.clockapp.features.alarms.presentation.composables.GlowyCancelButton
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@Composable
fun PlayAlarmScreenContent(
	dateTime: LocalDateTime,
	onStopAlarm: () -> Unit,
	onSnoozeAlarm: () -> Unit,
	modifier: Modifier = Modifier,
	labelText: String? = null,
	is24HrFormat: Boolean = true,
	isPreview: Boolean = false,
	onPreview: () -> Unit = {},
	textColorPrimary: Color = Color.White,
	textColorSecondary: Color = Color.White,
	buttonContentColor: Color = Color.Black,
	buttonContainerColor: Color = Color.White,
) {
	Box(
		modifier = modifier
			.padding(all = dimensionResource(R.dimen.alarms_screen_padding))
			.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		if (isPreview) {
			FloatingActionButton(
				onClick = onPreview,
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
				modifier = Modifier.align(Alignment.TopEnd),
				elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
				shape = MaterialTheme.shapes.large,
			) {
				Icon(
					imageVector = Icons.Outlined.RemoveRedEye,
					contentDescription = stringResource(R.string.preview_alarm_screen)
				)
			}
		}
		Column(
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			AlarmTimeText(
				time = dateTime.time,
				is24HrFormat = is24HrFormat,
				color = textColorPrimary
			)
			Text(
				text = dateTime.date.format(LocalDate.Formats.WEEK_MONTH_DAY),
				style = MaterialTheme.typography.headlineMedium,
				color = textColorPrimary,
				fontWeight = FontWeight.SemiBold
			)
			Spacer(modifier = Modifier.height(16.dp))
			labelText?.let {
				Text(
					text = labelText,
					style = MaterialTheme.typography.titleLarge,
					color = textColorSecondary,
					maxLines = 4,
					overflow = TextOverflow.Ellipsis,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(.6f),
				)
			}
		}
		Column(
			verticalArrangement = Arrangement.spacedBy(30.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.offset(y = (-20).dp)
		) {
			GlowyCancelButton(
				onClick = onStopAlarm,
				shape = CircleShape,
				enabled = !isPreview,
				isAnimated = !isPreview,
				containerColor = buttonContainerColor,
				contentColor = buttonContentColor,
			) {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = stringResource(R.string.action_stop),
				)
			}

			Button(
				onClick = onSnoozeAlarm,
				enabled = !isPreview,
				shape = MaterialTheme.shapes.extraLarge,
				colors = ButtonDefaults.buttonColors(
					containerColor = buttonContainerColor,
					disabledContainerColor = buttonContainerColor,
					contentColor = buttonContentColor,
					disabledContentColor = buttonContentColor,
				),
				elevation = ButtonDefaults.elevatedButtonElevation(),
				contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
				modifier = Modifier.sizeIn(minWidth = 150.dp)
			) {
				Text(
					text = stringResource(R.string.snooze_options_title),
					style = MaterialTheme.typography.titleMedium,
				)
			}
		}
	}
}

@Composable
private fun AlarmTimeText(
	time: LocalTime,
	modifier: Modifier = Modifier,
	is24HrFormat: Boolean = true,
	style: TextStyle = MaterialTheme.typography.displayLarge,
	fontSize: TextUnit = 70.sp,
	fontFamily: FontFamily = DownloadableFonts.INSTRUMENT_SANS,
	color: Color = Color.White,
) {
	if (is24HrFormat) {
		Text(
			text = time.format(LocalTime.Formats.HH_MM),
			style = style,
			fontSize = fontSize,
			fontFamily = fontFamily,
			textAlign = TextAlign.Center,
			color = color,
			letterSpacing = 2.sp, modifier = modifier
		)
	} else {

		val amMarker = stringResource(R.string.ante_meridian)
		val pmMarker = stringResource(R.string.post_meridian)

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = time.format(
					LocalTime.Format {
						amPmHour(padding = Padding.ZERO)
						char(':')
						minute(padding = Padding.ZERO)
					},
				),
				style = style,
				fontSize = fontSize,
				fontFamily = fontFamily,
				textAlign = TextAlign.Center,
				color = color,
				letterSpacing = 2.sp
			)
			Text(
				text = time.format(
					LocalTime.Format { amPmMarker(am = amMarker, pm = pmMarker) },
				),
				style = style,
				fontSize = 50.sp,
				fontFamily = fontFamily,
				textAlign = TextAlign.Center,
				color = color,
				letterSpacing = 2.sp
			)
		}
	}
}