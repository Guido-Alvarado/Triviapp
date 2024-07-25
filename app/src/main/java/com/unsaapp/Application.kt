package com.unsaapp

import android.app.Application
import android.widget.Toast
import com.google.android.gms.ads.MobileAds

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }

    override fun onTerminate() {
        Toast.makeText(
            applicationContext,
            "Adiós, la aplicación se ha cerrado.",
            Toast.LENGTH_SHORT
        ).show()
        super.onTerminate()
    }
}