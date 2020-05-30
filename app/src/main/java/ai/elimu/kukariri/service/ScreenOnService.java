package ai.elimu.kukariri.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import ai.elimu.kukariri.receiver.ScreenOnReceiver;

public class ScreenOnService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getName(), "onStartCommand");

        // Register receiver for detecting when the screen is turned on
        ScreenOnReceiver screenOnReceiver = new ScreenOnReceiver();
        registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        return super.onStartCommand(intent, flags, startId);
    }
}
