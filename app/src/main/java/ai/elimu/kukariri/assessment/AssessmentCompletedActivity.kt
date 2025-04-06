package ai.elimu.kukariri.assessment

import ai.elimu.kukariri.R
import ai.elimu.kukariri.util.MediaPlayerHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sackcentury.shinebuttonlib.ShineButton

class AssessmentCompletedActivity : AppCompatActivity() {
    private var shineButton: ShineButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.name, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_assessment_completed)

        shineButton = findViewById(R.id.assessmentCompletedShineButton)
    }

    override fun onStart() {
        Log.i(javaClass.name, "onStart")
        super.onStart()

        shineButton!!.postDelayed(object : Runnable {
            override fun run() {
                Log.i(javaClass.name, "run")
                shineButton!!.callOnClick()
                MediaPlayerHelper.play(applicationContext, R.raw.success)
            }
        }, 500)
    }
}
