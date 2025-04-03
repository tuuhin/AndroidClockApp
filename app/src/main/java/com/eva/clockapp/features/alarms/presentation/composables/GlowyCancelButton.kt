package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun GlowyCancelButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = Color.White,
	contentColor: Color = contentColorFor(containerColor),
	shape: Shape = CircleShape,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	isAnimated: Boolean = false,
	enabled: Boolean = true,
	size: DpSize = DpSize(56.dp, 56.dp),
	content: @Composable () -> Unit,
) {

	val infiniteTransition = rememberInfiniteTransition(label = "Infinite transition")

	val boxScaleAmount by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = 2f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 1000, easing = EaseInOut),
			repeatMode = RepeatMode.Reverse
		),
		label = "Ripple Indication for height",
	)

	val animatedRippleColor by infiniteTransition.animateColor(
		initialValue = Color.Transparent,
		targetValue = containerColor.copy(.3f),
		animationSpec = infiniteRepeatable(
			animation = keyframes {
				durationMillis = 2000
				Color.Transparent at 0 using FastOutLinearInEasing
				containerColor.copy(.3f) at 500 using EaseOut
				Color.Transparent at 1000 using FastOutLinearInEasing
			},
			repeatMode = RepeatMode.Restart
		),
		label = "Ripple colors"
	)

	Box(
		modifier = modifier.size(size * 2),
		contentAlignment = Alignment.Center
	) {
		// ripple container
		if (isAnimated) {
			Box(
				modifier = Modifier
					.size(size = size)
					.graphicsLayer {
						scaleX = boxScaleAmount
						scaleY = boxScaleAmount
					}
					.drawBehind {
						drawCircle(animatedRippleColor)
					},
			)
		}
		// clickable button
		Box(
			modifier = Modifier
				.size(size = size)
				.clip(shape = shape)
				.background(color = containerColor)
				.indication(interactionSource = interactionSource, indication = ripple())
				.clickable(role = Role.Button, enabled = enabled, onClick = onClick),
			contentAlignment = Alignment.Center
		) {
			CompositionLocalProvider(LocalContentColor provides contentColor) {
				content()
			}
		}
	}
}


@Preview
@Composable
fun CaptureButtonPreview() = ClockAppTheme {
	GlowyCancelButton(onClick = { }, isAnimated = true) {
		Icon(
			imageVector = Icons.Default.Close,
			contentDescription = stringResource(R.string.action_stop)
		)
	}
}