package com.eva.clockapp.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.eva.clockapp.R

private val provider = GoogleFont.Provider(
	providerAuthority = "com.google.android.gms.fonts",
	providerPackage = "com.google.android.gms",
	certificates = R.array.com_google_android_gms_fonts_certs
)

object DownloadableFonts {

	val CHELSEA_MARKET = FontFamily(
		Font(googleFont = GoogleFont("Chelsea Market"), fontProvider = provider)
	)

	val BUNGEE = FontFamily(
		Font(googleFont = GoogleFont("Bungee"), fontProvider = provider)
	)
}