plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.androidx.room)
	alias(libs.plugins.ksp)
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

	//kotlinx-library
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.datetime)

	// room
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	//koin
	implementation(libs.koin.android)

	//others
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.ui.text.google.fonts)

	//testing
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}