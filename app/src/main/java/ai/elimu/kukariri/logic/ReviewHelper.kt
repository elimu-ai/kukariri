package ai.elimu.kukariri.logic

import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson
import android.util.Log

object ReviewHelper {
    /**
     * Determines which of the previously learned Words are pending a review (based on [WordAssessmentEventGson]s).
     */
    fun getIdsOfWordsPendingReview(
        idsOfWordsInWordLearningEvents: Set<Long>,
        wordLearningEventGsons: List<WordLearningEventGson>,
        wordAssessmentEventGsons: List<WordAssessmentEventGson>
    ): Set<Long> {
        Log.i(ReviewHelper::class.java.name, "getIdsOfWordsPendingReview")

        val idsOfWordsPendingReview: MutableSet<Long> = HashSet()

        for (idOfWordInWordLearningEvent in idsOfWordsInWordLearningEvents) {
            var originalWordLearningEventGson: WordLearningEventGson? = null
            for (wordLearningEventGson in wordLearningEventGsons) {
                if (wordLearningEventGson.wordId == idOfWordInWordLearningEvent) {
                    originalWordLearningEventGson = wordLearningEventGson
                    break
                }
            }

            val wordAssessmentEventGsonsAssociatedWithLearningEvent: MutableList<WordAssessmentEventGson> =
                ArrayList()
            for (wordAssessmentEventGson in wordAssessmentEventGsons) {
                if (wordAssessmentEventGson.wordId == idOfWordInWordLearningEvent) {
                    wordAssessmentEventGsonsAssociatedWithLearningEvent.add(wordAssessmentEventGson)
                }
            }

            // Check if the Word has any pending assessment reviews
            val isReviewPending = originalWordLearningEventGson?.let {
                SpacedRepetitionHelper.isReviewPending(
                    originalWordLearningEventGson,
                    wordAssessmentEventGsonsAssociatedWithLearningEvent
                )
            } == true

            if (isReviewPending) {
                idsOfWordsPendingReview.add(idOfWordInWordLearningEvent)
            }
        }

        return idsOfWordsPendingReview
    }
}
