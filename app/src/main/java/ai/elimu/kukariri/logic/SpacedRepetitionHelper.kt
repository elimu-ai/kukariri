package ai.elimu.kukariri.logic

import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson
import java.util.Calendar

object SpacedRepetitionHelper {
    /**
     * Verifies that a [ai.elimu.model.v2.gson.content.WordGson] has been reviewed (with mastery) 8 times after
     * the original [WordLearningEventGson]:
     * • After 4 minutes
     * • After 16 minutes
     * • After 64 minutes (~1 hour)
     * • After 256 minutes (~4 hours)
     * • After 1,024 minutes (~17 hours)
     * • After 4,096 minutes (~3 days)
     * • After 16,384 minutes (~11 days)
     * • After 65,536 minutes (~46 days)
     *
     * If the student fails to demonstrate mastery during an assessment event, the sequence is restarted from the
     * beginning.
     *
     * @param wordLearningEventGson The _first_ time a [ai.elimu.model.v2.gson.content.WordGson] was learned. If
     * there are several [WordLearningEventGson]s for the same
     * [ai.elimu.model.v2.gson.content.WordGson], the oldest one is used.
     * @param wordAssessmentEventGsons List of assessments in _descending_ order, i.e. most recent events are first in the list.
     * @return `true` if the [ai.elimu.model.v2.gson.content.WordGson] has one or more pending reviews.
     */
    fun isReviewPending(
        wordLearningEventGson: WordLearningEventGson,
        wordAssessmentEventGsons: MutableList<WordAssessmentEventGson>
    ): Boolean {
        var isReviewPending = false

        if (wordAssessmentEventGsons.isEmpty()) {
            // No reviews have been performed

            val milliSecondsPassedSinceWordLearningEvent =
                Calendar.getInstance().getTimeInMillis() - wordLearningEventGson.timestamp
                    .getTimeInMillis()
            val minutesPassedSinceWordLearningEvent =
                (milliSecondsPassedSinceWordLearningEvent / 1000 / 60).toDouble()
            if (minutesPassedSinceWordLearningEvent >= 4) {
                isReviewPending = true
            }
        } else {
            // At least one review has already been performed

            var numberOfCorrectReviewsInSequence = 0
            for (i in wordAssessmentEventGsons.indices) {
                val wordAssessmentEventGson = wordAssessmentEventGsons[i]
                if (wordAssessmentEventGson.masteryScore == 1.00f) {
                    numberOfCorrectReviewsInSequence++
                } else {
                    break
                }
            }

            if (numberOfCorrectReviewsInSequence == 0) {
                // The most recent review was not mastered
                isReviewPending = true
            } else {
                // The most recent review was mastered

                val mostRecentWordAssessmentEventGson = wordAssessmentEventGsons[0]
                val milliSecondsPassedSinceMostRecentAssessmentEvent = Calendar.getInstance()
                    .getTimeInMillis() - mostRecentWordAssessmentEventGson.timestamp
                    .getTimeInMillis()
                val minutesPassedSinceMostRecentAssessmentEvent =
                    (milliSecondsPassedSinceMostRecentAssessmentEvent / 1000 / 60).toDouble()

                if (numberOfCorrectReviewsInSequence == 1) {
                    // Check if it's time for the 2nd review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 16) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 2) {
                    // Check if it's time for the 3rd review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 64) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 3) {
                    // Check if it's time for the 4th review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 256) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 4) {
                    // Check if it's time for the 5th review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 1024) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 5) {
                    // Check if it's time for the 6th review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 4096) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 6) {
                    // Check if it's time for the 7th review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 16384) {
                        isReviewPending = true
                    }
                } else if (numberOfCorrectReviewsInSequence == 7) {
                    // Check if it's time for the 8th review
                    if (minutesPassedSinceMostRecentAssessmentEvent >= 65536) {
                        isReviewPending = true
                    }
                }
            }
        }

        return isReviewPending
    }
}
