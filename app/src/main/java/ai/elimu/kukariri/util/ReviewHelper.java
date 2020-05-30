package ai.elimu.kukariri.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ai.elimu.kukariri.logic.SpacedRepetitionHelper;
import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;

public class ReviewHelper {

    /**
     * Determines which of the previously learned Words are pending a review (based on {@link WordAssessmentEventGson}s).
     */
    public static Set<Long> getIdsOfWordsPendingReview(
            Set<Long> idsOfWordsInWordLearningEvents,
            List<WordLearningEventGson> wordLearningEventGsons,
            List<WordAssessmentEventGson> wordAssessmentEventGsons
    ) {
        Log.i(ReviewHelper.class.getName(), "getIdsOfWordsPendingReview");

        Set<Long> idsOfWordsPendingReview = new HashSet<>();

        for (Long idOfWordInWordLearningEvent : idsOfWordsInWordLearningEvents) {
            WordLearningEventGson originalWordLearningEventGson = null;
            for (WordLearningEventGson wordLearningEventGson : wordLearningEventGsons) {
                if (wordLearningEventGson.getWordId().equals(idOfWordInWordLearningEvent)) {
                    originalWordLearningEventGson = wordLearningEventGson;
                    break;
                }
            }

            List<WordAssessmentEventGson> wordAssessmentEventGsonsAssociatedWithLearningEvent = new ArrayList<>();
            for (WordAssessmentEventGson wordAssessmentEventGson : wordAssessmentEventGsons) {
                if (wordAssessmentEventGson.getWordId().equals(idOfWordInWordLearningEvent)) {
                    wordAssessmentEventGsonsAssociatedWithLearningEvent.add(wordAssessmentEventGson);
                }
            }

            // Check if the Word has any pending assessment reviews
            boolean isReviewPending = SpacedRepetitionHelper.isReviewPending(originalWordLearningEventGson, wordAssessmentEventGsonsAssociatedWithLearningEvent);
            if (isReviewPending) {
                idsOfWordsPendingReview.add(idOfWordInWordLearningEvent);
            }
        }

        return idsOfWordsPendingReview;
    }
}
