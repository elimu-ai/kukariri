package ai.elimu.kukariri.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * This receiver is only used to trigger [BaseApplication.onCreate].
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(javaClass.name, "onReceive")
    }
}
