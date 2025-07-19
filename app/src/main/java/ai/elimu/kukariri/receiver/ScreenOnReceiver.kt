package ai.elimu.kukariri.receiver

import ai.elimu.kukariri.MainActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.Display

class ScreenOnReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(this::class.simpleName, "onReceive")

        Log.i(this::class.simpleName, "intent: $intent")
        Log.i(this::class.simpleName, "intent.action: ${intent.action}")

        // Do not proceed if the screen is not active
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        for (display in displayManager.displays) {
            Log.i(this::class.simpleName, "display: $display")
            if (display.state != Display.STATE_ON) {
                return
            }
        }

        // Launch the application
        val launchIntent = Intent(context, MainActivity::class.java)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }
}
