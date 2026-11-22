package ai.elimu.kukariri

import ai.elimu.kukariri.assessment.WordAssessmentActivity
import ai.elimu.kukariri.ui.KukaririApp
import ai.elimu.kukariri.ui.KukaririTheme
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

    private val letterSoundsViewModel: LetterSoundsViewModel by viewModels()
    private val familiarWordsViewModel: FamiliarWordsViewModel by viewModels()

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
                KukaririApp(letterSoundsViewModel, familiarWordsViewModel)
            }
        }
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        // Reload the masteries every time the student returns to the app
        letterSoundsViewModel.load()
        familiarWordsViewModel.load()
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
