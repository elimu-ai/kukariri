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

    /**
     * Test a time _before_ the time of the initial review (4 minutes).
     */
    @Test
    public void testIsReviewPending_3MinutesAfter() {
        Calendar calendar3MinutesAgo = Calendar.getInstance();
        calendar3MinutesAgo.add(Calendar.MINUTE, -3);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar3MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(false));
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    public void testIsReviewPending_5MinutesAfter() {
        Calendar calendar5MinutesAgo = Calendar.getInstance();
        calendar5MinutesAgo.add(Calendar.MINUTE, -5);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar5MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    public void testIsReviewPending_5MinutesAfter_1stReviewNotMastered() {
        Calendar calendar5MinutesAgo = Calendar.getInstance();
        calendar5MinutesAgo.add(Calendar.MINUTE, -5);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar5MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTimestamp(calendarNow);
        wordAssessmentEventGson.setMasteryScore(0.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(true));
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    public void testIsReviewPending_5MinutesAfter_1stReviewMastered() {
        Calendar calendar5MinutesAgo = Calendar.getInstance();
        calendar5MinutesAgo.add(Calendar.MINUTE, -5);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar5MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTimestamp(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(false));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_17MinutesAfter_MasteryOnFirstTry() {
        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar17MinutesAgo);

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTimestamp(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_17MinutesAfter_MasteryOnSecondTry() {
        Calendar calendar22MinutesAgo = Calendar.getInstance();
        calendar22MinutesAgo.add(Calendar.MINUTE, -22);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar22MinutesAgo);

        Calendar calendar18MinutesAgo = Calendar.getInstance();
        calendar18MinutesAgo.add(Calendar.MINUTE, -18);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar18MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(0.00f);

        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTimestamp(calendar17MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);

        // Store assessment events in _descending_ order
        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_17MinutesAfter_MasteryOnThirdTry() {
        Calendar calendar23MinutesAgo = Calendar.getInstance();
        calendar23MinutesAgo.add(Calendar.MINUTE, -23);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar23MinutesAgo);

        Calendar calendar19MinutesAgo = Calendar.getInstance();
        calendar19MinutesAgo.add(Calendar.MINUTE, -19);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar19MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(0.00f);

        Calendar calendar18MinutesAgo = Calendar.getInstance();
        calendar18MinutesAgo.add(Calendar.MINUTE, -18);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTimestamp(calendar18MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(0.00f);

        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordAssessmentEventGson wordAssessmentEventGsonThird = new WordAssessmentEventGson();
        wordAssessmentEventGsonThird.setWordId(1L);
        wordAssessmentEventGsonThird.setTimestamp(calendar17MinutesAgo);
        wordAssessmentEventGsonThird.setMasteryScore(1.00f);

        // Store assessment events in _descending_ order
        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonThird);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }

    /**
     * Test a time _before_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_false_15MinutesAfterFirstMastery() {
        Calendar calendar60MinutesAgo = Calendar.getInstance();
        calendar60MinutesAgo.add(Calendar.MINUTE, -60);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar60MinutesAgo);

        Calendar calendar15MinutesAgo = Calendar.getInstance();
        calendar15MinutesAgo.add(Calendar.MINUTE, -15);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar15MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_true_17MinutesAfterFirstMastery() {
        Calendar calendar60MinutesAgo = Calendar.getInstance();
        calendar60MinutesAgo.add(Calendar.MINUTE, -60);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar60MinutesAgo);

        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar17MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }

    /**
     * Test a time _before_ the time of the 3rd review (64 minutes).
     */
    @Test
    public void testIsReviewPending_false_63MinutesAfterSecondMastery() {
        Calendar calendar180MinutesAgo = Calendar.getInstance();
        calendar180MinutesAgo.add(Calendar.MINUTE, -180);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar180MinutesAgo);

        // 1st review (>=4 minutes after learning event)
        // 180 minutes ago - 120 minutes ago = 60 minutes passed
        Calendar calendar120MinutesAgo = Calendar.getInstance();
        calendar120MinutesAgo.add(Calendar.MINUTE, -120);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar120MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);

        // 2nd review (>=16 minutes after the 1st mastery)
        // 120 minutes ago - 63 minutes ago = 57
        Calendar calendar63MinutesAgo = Calendar.getInstance();
        calendar63MinutesAgo.add(Calendar.MINUTE, -63);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTimestamp(calendar63MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);

        // Store assessment events in _descending_ order
        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        // 3rd review (>=64 minutes after the 2nd mastery)
        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    /**
     * Test a time _after_ the time of the 3rd review (64 minutes).
     */
    @Test
    public void testIsReviewPending_true_65MinutesAfterSecondMastery() {
        Calendar calendar180MinutesAgo = Calendar.getInstance();
        calendar180MinutesAgo.add(Calendar.MINUTE, -180);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTimestamp(calendar180MinutesAgo);

        Calendar calendar120MinutesAgo = Calendar.getInstance();
        calendar120MinutesAgo.add(Calendar.MINUTE, -120);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTimestamp(calendar120MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);

        Calendar calendar65MinutesAgo = Calendar.getInstance();
        calendar65MinutesAgo.add(Calendar.MINUTE, -65);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTimestamp(calendar65MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);

        // Store assessment events in _descending_ order
        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }
}
