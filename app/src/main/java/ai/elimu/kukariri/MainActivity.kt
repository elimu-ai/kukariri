package ai.elimu.kukariri

import ai.elimu.kukariri.assessment.WordAssessmentActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val deviceLocale = Locale.getDefault()
        Log.i(TAG, "deviceLocale: $deviceLocale")
        val deviceLanguage = deviceLocale.language
        Log.i(TAG, "deviceLanguage: $deviceLanguage")
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        val wordAssessmentActivityIntent = Intent(
            applicationContext,
            WordAssessmentActivity::class.java
        )
        startActivity(wordAssessmentActivityIntent)
        finish()
    }
}
