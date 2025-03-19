package ai.elimu.kukariri.service

import ai.elimu.kukariri.receiver.ScreenOnReceiver
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class ScreenOnService : Service() {

    private lateinit var screenOnReceiver: ScreenOnReceiver

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(javaClass.name, "onStartCommand")

        // Register receiver for detecting when the screen is turned on
        screenOnReceiver = ScreenOnReceiver()
        registerReceiver(screenOnReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenOnReceiver)
        } catch (e: Exception) {
            Log.e("ScreenOnService", "Error unregistering receiver", e)
        }
    }
}
