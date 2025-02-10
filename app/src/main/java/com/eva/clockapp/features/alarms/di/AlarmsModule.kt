package com.eva.clockapp.features.alarms.di

import com.eva.clockapp.features.alarms.data.controllers.RingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.VibrationControllerImpl
import com.eva.clockapp.features.alarms.domain.controllers.RingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alarmsModule = module {

	// singletons
	singleOf(::VibrationControllerImpl) bind VibrationController::class
	singleOf(::RingtoneProviderImpl) bind RingtoneProvider::class

	// view models
	viewModelOf(::CreateAlarmViewModel)
	viewModelOf(::AlarmsViewModel)
}