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
        Calendar calendar61MinutesAgo = Calendar.getInstance();
        calendar61MinutesAgo.add(Calendar.MINUTE, -61);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar61MinutesAgo);

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

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(0.00f);
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

        List<WordAssessmentEventGson> wordAssessmentEventGsonList = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);
        wordAssessmentEventGsonList.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsonList), is(false));
    }

    @Test
    public void testIsReviewPending_5HoursAfter_MasteryOnFirstTry() {
        Calendar calendar5HoursAgo = Calendar.getInstance();
        calendar5HoursAgo.add(Calendar.HOUR, -24);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar5HoursAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendarNow = Calendar.getInstance();
        WordAssessmentEventGson wordAssessmentEventGson = new WordAssessmentEventGson();
        wordAssessmentEventGson.setWordId(1L);
        wordAssessmentEventGson.setTime(calendarNow);
        wordAssessmentEventGson.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGson);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    @Test
    public void testIsReviewPending_5HoursAfter_MasteryOnSecondTry() {
        Calendar calendar5HoursAgo = Calendar.getInstance();
        calendar5HoursAgo.add(Calendar.HOUR, -24);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar5HoursAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar5MinutesAgo = Calendar.getInstance();
        calendar5MinutesAgo.add(Calendar.MINUTE, -5);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar5MinutesAgo);
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

    @Test
    public void testIsReviewPending_5HoursAfter_MasteryOnThirdTry() {
        Calendar calendar5HoursAgo = Calendar.getInstance();
        calendar5HoursAgo.add(Calendar.HOUR, -5);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar5HoursAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar10MinutesAgo = Calendar.getInstance();
        calendar10MinutesAgo.add(Calendar.MINUTE, -10);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar10MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(0.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));

        Calendar calendar5MinutesAgo = Calendar.getInstance();
        calendar5MinutesAgo.add(Calendar.MINUTE, -5);
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar5MinutesAgo);
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

    @Test
    public void testIsReviewPending_false_3Hours59MinutesAfterFirstMastery() {
        Calendar calendar1DayAgo = Calendar.getInstance();
        calendar1DayAgo.add(Calendar.DAY_OF_YEAR, -1);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar1DayAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar3Hours59MinutesAgo = Calendar.getInstance();
        calendar3Hours59MinutesAgo.add(Calendar.MINUTE, -(3*60 + 59));
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar3Hours59MinutesAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    @Test
    public void testIsReviewPending_true_4Hours1MinuteAfterFirstMastery() {
        Calendar calendar1DayAgo = Calendar.getInstance();
        calendar1DayAgo.add(Calendar.DAY_OF_YEAR, -1);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar1DayAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar4Hours1MinuteAgo = Calendar.getInstance();
        calendar4Hours1MinuteAgo.add(Calendar.MINUTE, -(4*60 + 1));
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar4Hours1MinuteAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }

    @Test
    public void testIsReviewPending_false_15Hours59MinutesAfterSecondMastery() {
        Calendar calendar24HoursAgo = Calendar.getInstance();
        calendar24HoursAgo.add(Calendar.HOUR, -24);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar24HoursAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar22HoursAgo = Calendar.getInstance();
        calendar22HoursAgo.add(Calendar.HOUR, -22);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar22HoursAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        Calendar calendar15Hours59MinutesAgo = Calendar.getInstance();
        calendar15Hours59MinutesAgo.add(Calendar.MINUTE, -(15*60 + 59));
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar15Hours59MinutesAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(false));
    }

    @Test
    public void testIsReviewPending_true_16Hours1MinuteAfterSecondMastery() {
        Calendar calendar24HoursAgo = Calendar.getInstance();
        calendar24HoursAgo.add(Calendar.HOUR, -24);
        WordLearningEventGson wordLearningEventGson = new WordLearningEventGson();
        wordLearningEventGson.setWordId(1L);
        wordLearningEventGson.setTime(calendar24HoursAgo);

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        Calendar calendar22HoursAgo = Calendar.getInstance();
        calendar22HoursAgo.add(Calendar.HOUR, -22);
        WordAssessmentEventGson wordAssessmentEventGsonFirst = new WordAssessmentEventGson();
        wordAssessmentEventGsonFirst.setWordId(1L);
        wordAssessmentEventGsonFirst.setTime(calendar22HoursAgo);
        wordAssessmentEventGsonFirst.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst);

        Calendar calendar16Hours1MinuteAgo = Calendar.getInstance();
        calendar16Hours1MinuteAgo.add(Calendar.MINUTE, -(16*60 + 1));
        WordAssessmentEventGson wordAssessmentEventGsonSecond = new WordAssessmentEventGson();
        wordAssessmentEventGsonSecond.setWordId(1L);
        wordAssessmentEventGsonSecond.setTime(calendar16Hours1MinuteAgo);
        wordAssessmentEventGsonSecond.setMasteryScore(1.00f);
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond);

        assertThat(SpacedRepetitionHelper.isReviewPending(wordLearningEventGson, wordAssessmentEventGsons), is(true));
    }
}
