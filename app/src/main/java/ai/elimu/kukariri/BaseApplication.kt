package ai.elimu.kukariri

import ai.elimu.kukariri.service.ScreenOnService
import android.app.Application
import android.content.Intent
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        Log.i(javaClass.name, "onCreate")
        super.onCreate()

        // Start service for registering ScreenOnReceiver
        val screenOnServiceIntent = Intent(applicationContext, ScreenOnService::class.java)
        startService(screenOnServiceIntent)
    }
}
