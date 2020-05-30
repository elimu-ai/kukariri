package ai.elimu.kukariri;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import ai.elimu.kukariri.service.ScreenOnService;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        // Start service for registering ScreenOnReceiver
        Intent screenOnServiceIntent = new Intent(getApplicationContext(), ScreenOnService.class);
        startService(screenOnServiceIntent);
    }
}
