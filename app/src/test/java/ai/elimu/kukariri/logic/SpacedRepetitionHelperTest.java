package ai.elimu.kukariri.logic;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;

public class SpacedRepetitionHelperTest {

    @Test
    public void testIsReviewPending_59MinutesAfter() {
        Calendar calendar30MinutesAgo = Calendar.getInstance();
        calendar30MinutesAgo.add(Calendar.MINUTE, -59);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar30MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(false));
    }

    @Test
    public void testIsReviewPending_61MinutesAfter() {
        Calendar calendar30MinutesAgo = Calendar.getInstance();
        calendar30MinutesAgo.add(Calendar.MINUTE, -61);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar30MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
    }

    @Test
    public void testIsReviewPending_61MinutesAfter_1stReviewFailed() {
        Calendar calendar30MinutesAgo = Calendar.getInstance();
        calendar30MinutesAgo.add(Calendar.MINUTE, -61);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar30MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(0.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
    }

    @Test
    public void testIsReviewPending_61MinutesAfter_1stReviewNotMastered() {
        Calendar calendar30MinutesAgo = Calendar.getInstance();
        calendar30MinutesAgo.add(Calendar.MINUTE, -61);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar30MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(0.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
    }

    @Test
    public void testIsReviewPending_61MinutesAfter_1stReviewMastered() {
        Calendar calendar30MinutesAgo = Calendar.getInstance();
        calendar30MinutesAgo.add(Calendar.MINUTE, -61);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar30MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(false));
    }

//    @Test
//    public void testIsReviewPending_4HoursAfterFirstReview() {
//        Calendar calendar5HoursAgo = Calendar.getInstance();
//        calendar5HoursAgo.add(Calendar.HOUR, -1-4);
//        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
//        wordLearningEventGson.setWordId(1L);
//        wordLearningEventGson.setTime(calendar5HoursAgo);
//
//        Calendar calendarNow = Calendar.getInstance();
//        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
//        wordAssessmentEventGson.setWordId(1L);
//        wordAssessmentEventGson.setTime(calendarNow);
//        wordAssessmentEventGson.setMasteryScore(0.00f);
//
//        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
//        wordAssessmentEventGsonList.add(wordAssessmentEventGson);
//
//        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
//    }
}
