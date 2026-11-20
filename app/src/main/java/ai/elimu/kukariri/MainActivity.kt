package ai.elimu.kukariri

import ai.elimu.kukariri.assessment.WordAssessmentActivity
import ai.elimu.kukariri.ui.KukaririTheme
import ai.elimu.kukariri.ui.LetterSoundsScreen
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: LetterSoundsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        val deviceLocale = Locale.getDefault()
        Log.i(TAG, "deviceLocale: $deviceLocale")
        val deviceLanguage = deviceLocale.language
        Log.i(TAG, "deviceLanguage: $deviceLanguage")

        enableEdgeToEdge()
        setContent {
            KukaririTheme {
                LetterSoundsScreen(viewModel)
            }
        }
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        // Reload the letter-sound masteries every time the student returns to the app
        viewModel.load()
    }

//    override fun onStart() {
//        Log.i(TAG, "onStart")
//        super.onStart()
//
//        val wordAssessmentActivityIntent = Intent(
//            applicationContext,
//            WordAssessmentActivity::class.java
//        )
//        startActivity(wordAssessmentActivityIntent)
//        finish()
//    }
}
