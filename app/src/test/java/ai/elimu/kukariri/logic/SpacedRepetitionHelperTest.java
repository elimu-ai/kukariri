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
        wordLearningEventGson.setTime(calendar3MinutesAgo);

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
        wordLearningEventGson.setTime(calendar5MinutesAgo);

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
        wordLearningEventGson.setTime(calendar5MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(0.00f);
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
        wordLearningEventGson.setTime(calendar5MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);
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
        wordLearningEventGson.setTime(calendar17MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_17MinutesAfter_MasteryOnSecondTry() {
        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar17MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar1MinuteAgo = Calendar.getInstance();
        calendar1MinuteAgo.add(Calendar.MINUTE, -1);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar1MinuteAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(0.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendarNow);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    public void testIsReviewPending_17MinutesAfter_MasteryOnThirdTry() {
        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar17MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar2MinutesAgo = Calendar.getInstance();
        calendar2MinutesAgo.add(Calendar.MINUTE, -2);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar2MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(0.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));

        Calendar calendar1MinuteAgo = Calendar.getInstance();
        calendar1MinuteAgo.add(Calendar.MINUTE, -1);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar1MinuteAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(0.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGsonThird = new WordAssessmentEventGson();
        wordAssessmentEventGsonThird.setWordId(1L);
        wordAssessmentEventGsonThird.setTime(calendarNow);
        wordAssessmentEventGsonThird.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonThird);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
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
        wordLearningEventGson.setTime(calendar60MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar15MinutesAgo = Calendar.getInstance();
        calendar15MinutesAgo.add(Calendar.MINUTE, -15);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar15MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
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
        wordLearningEventGson.setTime(calendar60MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar17MinutesAgo = Calendar.getInstance();
        calendar17MinutesAgo.add(Calendar.MINUTE, -17);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar17MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
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
        wordLearningEventGson.setTime(calendar180MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar120MinutesAgo = Calendar.getInstance();
        calendar120MinutesAgo.add(Calendar.MINUTE, -120);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar120MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        Calendar calendar63MinutesAgo = Calendar.getInstance();
        calendar63MinutesAgo.add(Calendar.MINUTE, -63);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar63MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

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
        wordLearningEventGson.setTime(calendar180MinutesAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar120MinutesAgo = Calendar.getInstance();
        calendar120MinutesAgo.add(Calendar.MINUTE, -120);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar120MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        Calendar calendar65MinutesAgo = Calendar.getInstance();
        calendar65MinutesAgo.add(Calendar.MINUTE, -65);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar65MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }
}
