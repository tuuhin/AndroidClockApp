package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun RadioButtonWithTextItem(
	text: String,
	isSelected: Boolean,
	onClick: () -> Unit,
	enabled: Boolean = true,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	shape: Shape = MaterialTheme.shapes.medium,
	colors: RadioButtonColors = RadioButtonDefaults.colors(),
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.heightIn(min = 32.dp)
			.clip(shape)
			.clickable(role = Role.RadioButton, enabled = enabled, onClick = onClick)
	) {
		RadioButton(
			enabled = enabled,
			selected = isSelected,
			onClick = onClick,
			colors = colors,
		)
		Text(
			text = text,
			style = textStyle,
			color = if (enabled) LocalContentColor.current else colors.disabledUnselectedColor,
			modifier = Modifier.weight(1f)
		)
	}
}

@PreviewLightDark
@Composable
private fun RadioButtonWithTextItemPreview() = ClockAppTheme {
	Surface {
		RadioButtonWithTextItem(text = "Something", isSelected = false, onClick = {})
	}
}