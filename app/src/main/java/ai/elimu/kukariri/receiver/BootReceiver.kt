package ai.elimu.kukariri.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ai.elimu.kukariri.BaseApplication;

/**
 * This receiver is only used to trigger {@link BaseApplication#onCreate()}.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");
    }
}
