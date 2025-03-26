package com.eva.clockapp.features.alarms.di

import com.eva.clockapp.features.alarms.data.controllers.AlarmSoundPlayerImpl
import com.eva.clockapp.features.alarms.data.controllers.AlarmsControllerImpl
import com.eva.clockapp.features.alarms.data.controllers.VibrationControllerImpl
import com.eva.clockapp.features.alarms.data.controllers.WallpaperProviderImpl
import com.eva.clockapp.features.alarms.data.providers.AppRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.providers.ContentRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.providers.GalleryImageProviderImpl
import com.eva.clockapp.features.alarms.data.repository.AlarmsRepositoryImpl
import com.eva.clockapp.features.alarms.data.repository.RingtonesRepositoryImpl
import com.eva.clockapp.features.alarms.data.services.AlarmsNotificationProvider
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.GalleryImageProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperProvider
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.domain.repository.RingtonesRepository
import com.eva.clockapp.features.alarms.domain.use_case.ValidateAlarmUseCase
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmVibrationViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmsBackgroundViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmsSoundsViewmodel
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import com.eva.clockapp.features.alarms.presentation.gallery.GalleryScreenViewModel
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
	singleOf(::GalleryImageProviderImpl).bind<GalleryImageProvider>()

	// notification utils
	singleOf(::AlarmsNotificationProvider)

	//factory
	factoryOf(::AlarmsRepositoryImpl).bind<AlarmsRepository>()
	factoryOf(::RingtonesRepositoryImpl).bind<RingtonesRepository>()

	//use-cases
	factoryOf(::ValidateAlarmUseCase)

	// view models
	viewModelOf(::CreateAlarmViewModel)
	viewModelOf(::AlarmsViewModel)
	viewModelOf(::AlarmsBackgroundViewModel)
	viewModelOf(::GalleryScreenViewModel)
	viewModelOf(::AlarmsSoundsViewmodel)
	viewModelOf(::AlarmVibrationViewModel)
}