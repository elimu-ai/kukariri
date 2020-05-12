package ai.elimu.kukariri;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ai.elimu.kukariri.assessment.WordAssessmentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Locale deviceLocale = Locale.getDefault();
        Log.i(getClass().getName(), "deviceLocale: " + deviceLocale);
        String deviceLanguage = deviceLocale.getLanguage();
        Log.i(getClass().getName(), "deviceLanguage: " + deviceLanguage);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        Intent wordAssessmentActivityIntent = new Intent(getApplicationContext(), WordAssessmentActivity.class);
        startActivity(wordAssessmentActivityIntent);
        finish();
    }
}
