plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.androidx.room)
	alias(libs.plugins.ksp)
	alias(libs.plugins.google.protobuf)
}

android {
	namespace = "com.eva.clockapp"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.eva.clockapp"
		minSdk = 26
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
}

composeCompiler {

	reportsDestination = layout.buildDirectory.dir("compose_compiler")
	metricsDestination = layout.buildDirectory.dir("compose_compiler")

	val stabilityConfigFile = rootProject.layout.projectDirectory
		.file("stability_config.conf")
		.apply {
			val file = asFile
			if (!file.exists()) file.createNewFile()
		}

	stabilityConfigurationFiles.add(stabilityConfigFile)
}

room {
	schemaDirectory("$projectDir/schemas")
}


dependencies {
	//core
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	// compose
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)

	// navigation and lifecycle
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.service)

	//kotlinx-library
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.datetime)

	// room
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	//datastore
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.protobuf.javalite)
	implementation(libs.protobuf.kotlin.lite)

	//work manager
	implementation(libs.androidx.work.runtime.ktx)

	//coil
	implementation(libs.coil.compose)
	implementation(libs.coil.network.okhttp)

	//koin
	implementation(libs.koin.android)
	implementation(libs.koin.compose.viewmodel)
	implementation(libs.koin.workmanager)
	implementation(libs.koin.androidx.startup)

	//others
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.ui.text.google.fonts)
	implementation(libs.androidx.icons.extended)

	//testing
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}

protobuf {
	protoc {
		artifact = libs.protobuf.protoc.get().toString()
	}
	plugins {
		create("java") {
			artifact = libs.protobuf.gen.javalite.get().toString()
		}
	}

	generateProtoTasks {
		all().forEach { task ->
			task.plugins {
				create("java") {
					option("lite")
				}
				create("kotlin") {
					option("lite")
				}
			}
		}
	}
}