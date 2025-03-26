package ai.elimu.kukariri.logic;

import java.util.Calendar;
import java.util.List;

import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;

public class SpacedRepetitionHelper {

    /**
     * Verifies that a {@link ai.elimu.model.v2.gson.content.WordGson} has been reviewed (with mastery) 8 times after
     * the original {@link WordLearningEventGson}:
     *   • After 4 minutes
     *   • After 16 minutes
     *   • After 64 minutes (~1 hour)
     *   • After 256 minutes (~4 hours)
     *   • After 1,024 minutes (~17 hours)
     *   • After 4,096 minutes (~3 days)
     *   • After 16,384 minutes (~11 days)
     *   • After 65,536 minutes (~46 days)
     *
     * If the student fails to demonstrate mastery during an assessment event, the sequence is restarted from the
     * beginning.
     *
     * @param wordLearningEventGson The _first_ time a {@link ai.elimu.model.v2.gson.content.WordGson} was learned. If
     *                              there are several {@link WordLearningEventGson}s for the same
     *                              {@link ai.elimu.model.v2.gson.content.WordGson}, the oldest one is used.
     * @param wordAssessmentEventGsons List of assessments in _ascending_ order, i.e. oldest events are first in the list.
     * @return {@code true} if the {@link ai.elimu.model.v2.gson.content.WordGson} has one or more pending reviews.
     */
    public static boolean isReviewPending(WordLearningEventGson wordLearningEventGson, List<WordAssessmentEventGson> wordAssessmentEventGsons) {
        boolean isReviewPending = false;

        if (wordAssessmentEventGsons.isEmpty()) {
            // No reviews have been performed

            long milliSecondsPassedSinceWordLearningEvent = Calendar.getInstance().getTimeInMillis() - wordLearningEventGson.getTime().getTimeInMillis();
            Double minutesPassedSinceWordLearningEvent = Double.valueOf(milliSecondsPassedSinceWordLearningEvent / 1000 / 60);
            if (minutesPassedSinceWordLearningEvent >= 4) {
                isReviewPending = true;
            }
        } else {
            // At least one review has already been performed

            int numberOfCorrectReviewsInSequence = 0;
            for (int i = wordAssessmentEventGsons.size() - 1; i >= 0; i--) {
                WordAssessmentEventGson wordAssessmentEventGson = wordAssessmentEventGsons.get(i);
                if (wordAssessmentEventGson.getMasteryScore() == 1.00f) {
                    numberOfCorrectReviewsInSequence++;
                } else {
                    break;
                }
            }

            if (numberOfCorrectReviewsInSequence == 0) {
                // The most recent review was not mastered
                isReviewPending = true;
            } else {
                // The most recent review was mastered

                WordAssessmentEventGson previousWordAssessmentEventGson = wordAssessmentEventGsons.get(wordAssessmentEventGsons.size() - 1);
                long milliSecondsPassedSincePreviousAssessmentEvent = Calendar.getInstance().getTimeInMillis() - previousWordAssessmentEventGson.getTime().getTimeInMillis();
                Double minutesPassedSincePreviousAssessmentEvent = Double.valueOf(milliSecondsPassedSincePreviousAssessmentEvent / 1000 / 60);

                if (numberOfCorrectReviewsInSequence == 1) {
                    // Check if it's time for the 2nd review
                    if (minutesPassedSincePreviousAssessmentEvent >= 16) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 2) {
                    // Check if it's time for the 3rd review
                    if (minutesPassedSincePreviousAssessmentEvent >= 64) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 3) {
                    // Check if it's time for the 4th review
                    if (minutesPassedSincePreviousAssessmentEvent >= 256) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 4) {
                    // Check if it's time for the 5th review
                    if (minutesPassedSincePreviousAssessmentEvent >= 1_024) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 5) {
                    // Check if it's time for the 6th review
                    if (minutesPassedSincePreviousAssessmentEvent >= 4_096) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 6) {
                    // Check if it's time for the 7th review
                    if (minutesPassedSincePreviousAssessmentEvent >= 16_384) {
                        isReviewPending = true;
                    }
                } else if (numberOfCorrectReviewsInSequence == 7) {
                    // Check if it's time for the 8th review
                    if (minutesPassedSincePreviousAssessmentEvent >= 65_536) {
                        isReviewPending = true;
                    }
                }
            }
        }

        return isReviewPending;
    }
}
