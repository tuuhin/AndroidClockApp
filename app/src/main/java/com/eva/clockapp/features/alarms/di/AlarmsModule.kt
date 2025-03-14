package com.eva.clockapp.features.alarms.di

import com.eva.clockapp.features.alarms.data.controllers.AlarmSoundPlayerImpl
import com.eva.clockapp.features.alarms.data.controllers.AlarmsControllerImpl
import com.eva.clockapp.features.alarms.data.controllers.AppRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.ContentRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.VibrationControllerImpl
import com.eva.clockapp.features.alarms.data.controllers.WallpaperProviderImpl
import com.eva.clockapp.features.alarms.data.repository.AlarmsRepositoryImpl
import com.eva.clockapp.features.alarms.data.services.AlarmsNotificationProvider
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperProvider
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.domain.use_case.RingtoneProviderUseCase
import com.eva.clockapp.features.alarms.domain.use_case.ValidateAlarmUseCase
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmsBackgroundViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alarmsModule = module {

	// singletons
	singleOf(::VibrationControllerImpl).bind<VibrationController>()
	singleOf(::ContentRingtoneProviderImpl).bind<ContentRingtoneProvider>()
	singleOf(::AppRingtoneProviderImpl).bind<AppRingtoneProvider>()
	singleOf(::AlarmSoundPlayerImpl).bind<AlarmsSoundPlayer>()
	singleOf(::AlarmsControllerImpl).bind<AlarmsController>()
	singleOf(::WallpaperProviderImpl).bind<WallpaperProvider>()

	// notification utils
	singleOf(::AlarmsNotificationProvider)

	//factory
	factoryOf(::AlarmsRepositoryImpl).bind<AlarmsRepository>()
	factoryOf(::RingtoneProviderUseCase)
	factoryOf(::ValidateAlarmUseCase)

	// view models
	viewModelOf(::CreateAlarmViewModel)
	viewModelOf(::AlarmsViewModel)
	viewModelOf(::AlarmsBackgroundViewModel)
}