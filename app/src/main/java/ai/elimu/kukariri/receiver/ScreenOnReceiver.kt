package ai.elimu.kukariri.receiver

import ai.elimu.analytics.utils.EventProviderUtil
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllWordGsons
import ai.elimu.kukariri.BuildConfig
import ai.elimu.kukariri.MainActivity
import ai.elimu.kukariri.logic.ReviewHelper
import ai.elimu.model.v2.enums.content.WordType
import ai.elimu.model.v2.gson.content.WordGson
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.Display

class ScreenOnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(javaClass.getName(), "onReceive")

        Log.i(javaClass.getName(), "intent: " + intent)
        Log.i(javaClass.getName(), "intent.getAction(): " + intent.getAction())

        // Do not proceed if the screen is not active
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        for (display in displayManager.getDisplays()) {
            Log.i(javaClass.getName(), "display: " + display)
            if (display.getState() != Display.STATE_ON) {
                return
            }
        }

        // Get a list of the Words that have been previously learned
        val wordLearningEventGsons = EventProviderUtil.getWordLearningEventGsons(
            context,
            BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Get a set of the Words that have been previously learned
        val idsOfWordsInWordLearningEvents = EventProviderUtil.getIdsOfWordsInWordLearningEvents(
            context,
            BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Get a list of assessment events for the words that have been previously learned
        val wordAssessmentEventGsons = EventProviderUtil.getWordAssessmentEventGsons(
            context,
            BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        val idsOfWordsPendingReview = ReviewHelper.getIdsOfWordsPendingReview(
            idsOfWordsInWordLearningEvents,
            wordLearningEventGsons,
            wordAssessmentEventGsons
        )
        Log.i(
            javaClass.getName(),
            "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size
        )

        // Get list of adjectives/nouns/verbs pending review
        val wordGsonsPendingReview: MutableList<WordGson?> = ArrayList<WordGson?>()
        val allWordGsons: List<WordGson> =
            getAllWordGsons(context, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID)
        for (wordGson in allWordGsons) {
            if (idsOfWordsPendingReview.contains(wordGson.getId())) {
                // Only include adjectives/nouns/verbs
                if ((wordGson.getWordType() == WordType.ADJECTIVE)
                    || (wordGson.getWordType() == WordType.NOUN)
                    || (wordGson.getWordType() == WordType.VERB)
                ) {
                    wordGsonsPendingReview.add(wordGson)
                }
            }
        }
        Log.i(javaClass.getName(), "wordGsonsPendingReview.size(): " + wordGsonsPendingReview.size)
        if (!wordGsonsPendingReview.isEmpty()) {
            // Launch the application
            val launchIntent = Intent(context, MainActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    }
}
