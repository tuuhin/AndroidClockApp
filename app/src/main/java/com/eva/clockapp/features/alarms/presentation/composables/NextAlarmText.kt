package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import kotlin.time.Duration

@Composable
fun NextAlarmText(
	duration: Duration? = null,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current

	val text = remember(duration) {
		duration?.let {
			val days = duration.inWholeDays
			val hours = duration.inWholeHours % 24
			val minutes = duration.inWholeMinutes % 60

			val alamText = buildString {
				when {
					days > 0 -> append("$days d ")
					hours > 0 -> append("$hours h")
					minutes > 0 -> append("$minutes m")
					else -> return@remember context.getString(R.string.next_alarm_within_one_min)
				}
			}
			context.getString(R.string.next_alarms_at_time, alamText)
		} ?: context.getString(R.string.alarms_turned_off)
	}

	Box(
		modifier = modifier.heightIn(min = 100.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = text,
			color = MaterialTheme.colorScheme.primary,
			style = MaterialTheme.typography.headlineMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth(.75f)
		)
	}
}