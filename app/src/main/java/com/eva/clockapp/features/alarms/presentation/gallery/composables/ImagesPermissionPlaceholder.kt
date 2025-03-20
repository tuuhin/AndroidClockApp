package com.eva.clockapp.features.alarms.presentation.gallery.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun ImagesPermissionPlaceholder(
	onAllowPermission: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Image(
			painter = painterResource(R.drawable.ic_image),
			contentDescription = "Images",
			colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
			modifier = Modifier.size(200.dp)
		)
		Spacer(modifier = Modifier.height(12.dp))
		Text(
			text = stringResource(R.string.read_images_permission_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.onSurface
		)
		Text(
			text = stringResource(R.string.read_images_permission_desc),
			style = MaterialTheme.typography.labelLarge,
			textAlign = TextAlign.Center,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(12.dp))
		Button(
			onClick = onAllowPermission,
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.widthIn(min = 200.dp)
		) {
			Text(text = stringResource(R.string.action_allow_permission))
		}
	}
}

@PreviewLightDark
@Composable
private fun ImagesPermissionPlaceHolderPreview() = ClockAppTheme {
	Surface {
		ImagesPermissionPlaceholder(
			onAllowPermission = {},
			modifier = Modifier.padding(12.dp)
		)
	}
}