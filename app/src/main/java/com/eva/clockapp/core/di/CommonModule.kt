package com.eva.clockapp.core.di

import com.eva.clockapp.core.database.ClockAppDatabase
import com.eva.clockapp.features.alarms.data.database.AlarmsDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
	//database
	single<ClockAppDatabase> { ClockAppDatabase.createDatabase(context = androidContext()) }
	single<AlarmsDao> { get<ClockAppDatabase>().alarmsDao() }
}