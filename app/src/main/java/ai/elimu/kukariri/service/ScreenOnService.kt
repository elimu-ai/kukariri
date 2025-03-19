package ai.elimu.kukariri.service

import ai.elimu.kukariri.receiver.ScreenOnReceiver
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class ScreenOnService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(javaClass.name, "onStartCommand")

        // Register receiver for detecting when the screen is turned on
        val screenOnReceiver = ScreenOnReceiver()
        registerReceiver(screenOnReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))

        return super.onStartCommand(intent, flags, startId)
    }
}
