package com.eva.clockapp.features.alarms.domain.use_case

data class Validator(
	val message: String? = null,
	val isValid: Boolean,
)