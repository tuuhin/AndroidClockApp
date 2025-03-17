package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.WEEK_MONTH_DAY
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

@Composable
fun PlayAlarmScreenContent(
	dateTime: LocalDateTime,
	onStopAlarm: () -> Unit,
	onSnoozeAlarm: () -> Unit,
	modifier: Modifier = Modifier,
	labelText: String? = null,
	isActionEnabled: Boolean = true,
	textColorPrimary: Color = Color.White,
	textColorSecondary: Color = Color.White,
	textShadowColor: Color = Color.White,
	buttonContentColor: Color = Color.Black,
	buttonContainerColor: Color = Color.White,
) {
	Box(
		modifier = modifier
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
				style = MaterialTheme.typography.displayLarge.copy(
					fontSize = 80.sp,
					shadow = Shadow(color = textShadowColor, blurRadius = 4f)
				),
				fontFamily = DownloadableFonts.BUNGEE,
				color = textColorPrimary,
			)
			Text(
				text = dateTime.date.format(LocalDate.Formats.WEEK_MONTH_DAY),
				style = MaterialTheme.typography.headlineMedium,
				color = textColorSecondary,
			)
			labelText?.let {
				Text(
					text = labelText,
					style = MaterialTheme.typography.headlineMedium,
					color = textColorSecondary,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					textAlign = TextAlign.Center,
					modifier = Modifier.widthIn(max = 140.dp),
				)
			}
		}
		Column(
			verticalArrangement = Arrangement.spacedBy(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.offset(y = (-20).dp)
		) {
			FloatingActionButton(
				onClick = onStopAlarm,
				shape = CircleShape,
				containerColor = buttonContainerColor,
				contentColor = buttonContentColor,
				elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
			) {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = stringResource(R.string.action_stop)
				)
			}
			Button(
				onClick = onSnoozeAlarm,
				enabled = isActionEnabled,
				shape = MaterialTheme.shapes.extraLarge,
				colors = ButtonDefaults.buttonColors(
					containerColor = buttonContainerColor,
					disabledContainerColor = buttonContainerColor,
					contentColor = buttonContentColor,
					disabledContentColor = buttonContentColor,
				),
				elevation = ButtonDefaults.elevatedButtonElevation(),
				contentPadding = PaddingValues(vertical = 18.dp),
				modifier = Modifier.sizeIn(minWidth = 200.dp)
			) {
				Text(
					text = stringResource(R.string.snooze_options_title),
					style = MaterialTheme.typography.titleMedium
				)
			}
		}
	}
}