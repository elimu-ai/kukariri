package ai.elimu.kukariri.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        Log.i(getClass().getName(), "intent: " + intent);
        Log.i(getClass().getName(), "intent.getAction(): " + intent.getAction());
    }
}
