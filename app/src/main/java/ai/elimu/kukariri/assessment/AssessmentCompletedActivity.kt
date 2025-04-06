package ai.elimu.kukariri.assessment;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sackcentury.shinebuttonlib.ShineButton;

import ai.elimu.kukariri.R;
import ai.elimu.kukariri.util.MediaPlayerHelper;

public class AssessmentCompletedActivity extends AppCompatActivity {

    private ShineButton shineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_completed);

        shineButton = findViewById(R.id.assessmentCompletedShineButton);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        shineButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "run");
                shineButton.callOnClick();
                MediaPlayerHelper.play(getApplicationContext(), R.raw.success);
            }
        }, 500);
    }
}
