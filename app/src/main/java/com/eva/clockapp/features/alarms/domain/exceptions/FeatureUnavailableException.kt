package com.eva.clockapp.features.alarms.domain.exceptions

class FeatureUnavailableException :
	Exception("Selected feature is unavailable maybe not supported by this api")