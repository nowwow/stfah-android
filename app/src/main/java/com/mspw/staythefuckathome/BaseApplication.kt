package com.mspw.staythefuckathome

import android.app.Application
import com.facebook.stetho.Stetho

class BaseApplication : Application() {

    val appContainer = AppContainer()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}