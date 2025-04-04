package com.eva.clockapp.core.utils

import kotlin.math.pow
import kotlin.math.round

fun Float.roundToNDecimals(decimal: Int = 1): Float = roundToNDecimals(decimal.toUInt())

private fun Float.roundToNDecimals(decimal: UInt = 1u): Float {
	require(decimal >= 0u) { "Decimal places cannot be negative." }
	val multiplier = 10.0.pow(decimal.toDouble())
	return (round(this * multiplier) / multiplier).toFloat()
}