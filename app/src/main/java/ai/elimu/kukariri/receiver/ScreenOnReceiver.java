package ai.elimu.kukariri.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ai.elimu.analytics.utils.EventProviderUtil;
import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.kukariri.BuildConfig;
import ai.elimu.kukariri.MainActivity;
import ai.elimu.kukariri.logic.ReviewHelper;
import ai.elimu.model.enums.content.WordType;
import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;
import ai.elimu.model.v2.gson.content.WordGson;

public class ScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        Log.i(getClass().getName(), "intent: " + intent);
        Log.i(getClass().getName(), "intent.getAction(): " + intent.getAction());

        // Do not proceed if the screen is not active
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : displayManager.getDisplays()) {
            Log.i(getClass().getName(), "display: " + display);
            if (display.getState() != Display.STATE_ON) {
                return;
            }
        }

        // Get a list of the Words that have been previously learned
        List<WordLearningEventGson> wordLearningEventGsons = EventProviderUtil.getWordLearningEventGsons(context, BuildConfig.ANALYTICS_APPLICATION_ID);

        // Get a set of the Words that have been previously learned
        Set<Long> idsOfWordsInWordLearningEvents = EventProviderUtil.getIdsOfWordsInWordLearningEvents(context, BuildConfig.ANALYTICS_APPLICATION_ID);

        // Get a list of assessment events for the words that have been previously learned
        List<WordAssessmentEventGson> wordAssessmentEventGsons = EventProviderUtil.getWordAssessmentEventGsons(idsOfWordsInWordLearningEvents, context, BuildConfig.ANALYTICS_APPLICATION_ID);

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        Set<Long> idsOfWordsPendingReview = ReviewHelper.getIdsOfWordsPendingReview(idsOfWordsInWordLearningEvents, wordLearningEventGsons, wordAssessmentEventGsons);
        Log.i(getClass().getName(), "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size());

        // Get list of adjectives/nouns/verbs pending review
        List<WordGson> wordGsonsPendingReview = new ArrayList<>();
        List<WordGson> allWordGsons = ContentProviderHelper.getWordGsons(context, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        for (WordGson wordGson : allWordGsons) {
            if (idsOfWordsPendingReview.contains(wordGson.getId())) {
                // Only include adjectives/nouns/verbs
                if (       (wordGson.getWordType() == WordType.ADJECTIVE)
                        || (wordGson.getWordType() == WordType.NOUN)
                        || (wordGson.getWordType() == WordType.VERB)
                ) {
                    wordGsonsPendingReview.add(wordGson);
                }
            }
        }
        Log.i(getClass().getName(), "wordGsonsPendingReview.size(): " + wordGsonsPendingReview.size());
        if (!wordGsonsPendingReview.isEmpty()) {
            // Launch the application
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }
}
