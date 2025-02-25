package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun NoAlarmsFoundPlaceHolder(
	onCreateNew: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_alam_clock),
			contentDescription = "Alarm Clock",
			colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
			modifier = Modifier.size(120.dp)
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.no_alarm_title),
			style = MaterialTheme.typography.headlineSmall,
			color = MaterialTheme.colorScheme.onBackground
		)
		Text(
			text = stringResource(R.string.no_alarm_desc),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.outline
		)
		Spacer(modifier = Modifier.height(12.dp))
		Button(
			onClick = onCreateNew,
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.sizeIn(minWidth = 180.dp)
		) {
			Text(
				text = stringResource(R.string.create_action),
				style = MaterialTheme.typography.titleMedium
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun NoAlarmsFoundPlaceHolderPreview() = ClockAppTheme {
	Surface {
		NoAlarmsFoundPlaceHolder(onCreateNew = {})
	}
}