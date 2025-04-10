package ai.elimu.kukariri.assessment

import ai.elimu.kukariri.R
import ai.elimu.kukariri.databinding.ActivityAssessmentCompletedBinding
import ai.elimu.kukariri.util.MediaPlayerHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class AssessmentCompletedActivity : AppCompatActivity() {

    private val TAG = javaClass.name
    private lateinit var binding: ActivityAssessmentCompletedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityAssessmentCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        binding.assessmentCompletedShineButton.postDelayed({
            Log.i(TAG, "run")
            binding.assessmentCompletedShineButton.callOnClick()
            MediaPlayerHelper.play(applicationContext, R.raw.success)
        }, 500)
    }
}
