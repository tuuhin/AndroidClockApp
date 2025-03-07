package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CircularRangedNumberPicker(
	range: IntRange,
	onFocusItem: (Int) -> Unit,
	startIndex: Int = range.first,
	elementSize: DpSize = DpSize(64.dp, 64.dp),
	modifier: Modifier = Modifier,
	hapticEffectEnabled: Boolean = true,
	scrollEnabled: Boolean = true,
	isInfinite: Boolean = true,
	containerColor: Color = MaterialTheme.colorScheme.surface,
	scrollContentColor: Color = MaterialTheme.colorScheme.primary,
	contentColor: Color = MaterialTheme.colorScheme.onBackground,
	lazyListState: LazyListState = rememberLazyListState(),
	content: @Composable LazyItemScope.(Int) -> Unit,
) {
	// selected number  should be in the given range
	check(
		value = startIndex in range,
		lazyMessage = { "Selected index should be inside range index:$startIndex range : $range" },
	)

	val haptic = LocalHapticFeedback.current
	val updatedOnFocusItem by rememberUpdatedState(onFocusItem)

	val itemCount = remember(range) { range.last - range.first + 1 }
	val containerSize = remember(elementSize) {
		DpSize(elementSize.width, elementSize.height * 3)
	}

	val isScrollInProgress by remember(lazyListState) {
		derivedStateOf { lazyListState.isScrollInProgress }
	}

	LaunchedEffect(key1 = itemCount, key2 = startIndex) {
		// if endless scroll to a certain section beforehand
		val scrollDestination = if (isInfinite) startIndex + (1_000 * itemCount) - 1 else startIndex
		lazyListState.scrollToItem(scrollDestination)

		// update the values on first configure
		val index = calculateIndexToFocus(lazyListState, containerSize.height)
		val indexToFocus = (index + 1) % itemCount
		updatedOnFocusItem(indexToFocus)
	}

	LaunchedEffect(key1 = isScrollInProgress) {
		if (isScrollInProgress) return@LaunchedEffect

		// update the values when the scroll is changes
		val index = calculateIndexToFocus(lazyListState, containerSize.height)
		val indexToFocus = (index + 1) % itemCount

		if (lazyListState.firstVisibleItemScrollOffset != 0)
			lazyListState.animateScrollToItem(index, 0)

		updatedOnFocusItem(indexToFocus)
	}

	LaunchedEffect(key1 = lazyListState, key2 = hapticEffectEnabled) {
		if (!hapticEffectEnabled) return@LaunchedEffect
		// collects if the first item of the lazy list is visible
		snapshotFlow { lazyListState.firstVisibleItemIndex }
			.collectLatest {
				// then perform long press
				haptic.performHapticFeedback(HapticFeedbackType.LongPress)
			}
	}


	val coloredContentColor by animateColorAsState(
		targetValue = if (isScrollInProgress) scrollContentColor else contentColor,
	)

	val overlayBlendMode = if (isSystemInDarkTheme()) BlendMode.Darken
	else BlendMode.Lighten

	Box(
		modifier = modifier
			.size(containerSize)
			.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
		contentAlignment = Alignment.Center
	) {
		CompositionLocalProvider(LocalContentColor provides coloredContentColor) {
			LazyColumn(
				state = lazyListState,
				flingBehavior = rememberSnapFlingBehavior(lazyListState),
				userScrollEnabled = scrollEnabled,
				modifier = Modifier.fillMaxSize()
			) {
				items(
					count = if (isInfinite) Int.MAX_VALUE else itemCount + 2,
				) { idx ->
					Box(
						modifier = Modifier.size(elementSize),
						contentAlignment = Alignment.Center,
					) {
						if (isInfinite) content(idx % itemCount)
						else if (idx >= 1 && idx < itemCount + 1)
							content((idx - 1) % itemCount)
					}
				}
			}
		}
		Canvas(
			modifier = Modifier.fillMaxSize()
		) {
			drawRect(
				color = containerColor,
				topLeft = Offset.Zero,
				size = elementSize.toSize(),
				alpha = .4f,
				blendMode = overlayBlendMode,
			)
			drawRect(
				color = containerColor,
				topLeft = Offset(0f, elementSize.height.toPx() * 2),
				size = elementSize.toSize(),
				alpha = .4f,
				blendMode = overlayBlendMode,
			)
		}
	}
}

private fun calculateIndexToFocus(state: LazyListState, height: Dp): Int {
	// Get the current visible item
	val currentItem = state.layoutInfo.visibleItemsInfo.firstOrNull() ?: return 0
	// visible item index
	val idx = currentItem.index
	// if the offset is crossed a certain amount then use the next index
	if (currentItem.offset != 0 && currentItem.offset <= -height.value * .3f) return idx + 1
	// otherwise use the same index
	return idx

}


@PreviewLightDark
@Composable
private fun RangedInfiniteNumberPickerPreview() = ClockAppTheme {
	Surface {
		CircularRangedNumberPicker(
			range = 0..10,
			onFocusItem = {},
		) { idx ->
			Text("$idx")
		}
	}
}