package ai.elimu.kukariri.logic

import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class SpacedRepetitionHelperTest {
    /**
     * Test a time _before_ the time of the initial review (4 minutes).
     */
    @Test
    fun testIsReviewPending_3MinutesAfter() {
        val calendar3MinutesAgo = Calendar.getInstance()
        calendar3MinutesAgo.add(Calendar.MINUTE, -3)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar3MinutesAgo

        val wordAssessmentEventGsonList: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsonList
            ), CoreMatchers.`is`<Boolean?>(false)
        )
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    fun testIsReviewPending_5MinutesAfter() {
        val calendar5MinutesAgo = Calendar.getInstance()
        calendar5MinutesAgo.add(Calendar.MINUTE, -5)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar5MinutesAgo

        val wordAssessmentEventGsonList: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsonList
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    fun testIsReviewPending_5MinutesAfter_1stReviewNotMastered() {
        val calendar5MinutesAgo = Calendar.getInstance()
        calendar5MinutesAgo.add(Calendar.MINUTE, -5)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar5MinutesAgo

        val calendarNow = Calendar.getInstance()
        val wordAssessmentEventGson = WordAssessmentEventGson()
        wordAssessmentEventGson.wordId = 1L
        wordAssessmentEventGson.timestamp = calendarNow
        wordAssessmentEventGson.masteryScore = 0.00f

        val wordAssessmentEventGsonList: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsonList.add(wordAssessmentEventGson)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsonList
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }

    /**
     * Test a time _after_ the time of the initial review (4 minutes).
     */
    @Test
    fun testIsReviewPending_5MinutesAfter_1stReviewMastered() {
        val calendar5MinutesAgo = Calendar.getInstance()
        calendar5MinutesAgo.add(Calendar.MINUTE, -5)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar5MinutesAgo

        val calendarNow = Calendar.getInstance()
        val wordAssessmentEventGson = WordAssessmentEventGson()
        wordAssessmentEventGson.wordId = 1L
        wordAssessmentEventGson.timestamp = calendarNow
        wordAssessmentEventGson.masteryScore = 1.00f

        val wordAssessmentEventGsonList: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsonList.add(wordAssessmentEventGson)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsonList
            ), CoreMatchers.`is`<Boolean?>(false)
        )
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    fun testIsReviewPending_17MinutesAfter_MasteryOnFirstTry() {
        val calendar17MinutesAgo = Calendar.getInstance()
        calendar17MinutesAgo.add(Calendar.MINUTE, -17)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar17MinutesAgo

        val calendarNow = Calendar.getInstance()
        val wordAssessmentEventGson = WordAssessmentEventGson()
        wordAssessmentEventGson.wordId = 1L
        wordAssessmentEventGson.timestamp = calendarNow
        wordAssessmentEventGson.masteryScore = 1.00f

        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGson)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(false)
        )
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    fun testIsReviewPending_17MinutesAfter_MasteryOnSecondTry() {
        val calendar22MinutesAgo = Calendar.getInstance()
        calendar22MinutesAgo.add(Calendar.MINUTE, -22)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar22MinutesAgo

        val calendar18MinutesAgo = Calendar.getInstance()
        calendar18MinutesAgo.add(Calendar.MINUTE, -18)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar18MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 0.00f

        val calendar17MinutesAgo = Calendar.getInstance()
        calendar17MinutesAgo.add(Calendar.MINUTE, -17)
        val wordAssessmentEventGsonSecond = WordAssessmentEventGson()
        wordAssessmentEventGsonSecond.wordId = 1L
        wordAssessmentEventGsonSecond.timestamp = calendar17MinutesAgo
        wordAssessmentEventGsonSecond.masteryScore = 1.00f

        // Store assessment events in _descending_ order
        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond)
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    fun testIsReviewPending_17MinutesAfter_MasteryOnThirdTry() {
        val calendar23MinutesAgo = Calendar.getInstance()
        calendar23MinutesAgo.add(Calendar.MINUTE, -23)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar23MinutesAgo

        val calendar19MinutesAgo = Calendar.getInstance()
        calendar19MinutesAgo.add(Calendar.MINUTE, -19)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar19MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 0.00f

        val calendar18MinutesAgo = Calendar.getInstance()
        calendar18MinutesAgo.add(Calendar.MINUTE, -18)
        val wordAssessmentEventGsonSecond = WordAssessmentEventGson()
        wordAssessmentEventGsonSecond.wordId = 1L
        wordAssessmentEventGsonSecond.timestamp = calendar18MinutesAgo
        wordAssessmentEventGsonSecond.masteryScore = 0.00f

        val calendar17MinutesAgo = Calendar.getInstance()
        calendar17MinutesAgo.add(Calendar.MINUTE, -17)
        val wordAssessmentEventGsonThird = WordAssessmentEventGson()
        wordAssessmentEventGsonThird.wordId = 1L
        wordAssessmentEventGsonThird.timestamp = calendar17MinutesAgo
        wordAssessmentEventGsonThird.masteryScore = 1.00f

        // Store assessment events in _descending_ order
        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonThird)
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond)
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }

    /**
     * Test a time _before_ the time of the 2nd review (16 minutes).
     */
    @Test
    fun testIsReviewPending_false_15MinutesAfterFirstMastery() {
        val calendar60MinutesAgo = Calendar.getInstance()
        calendar60MinutesAgo.add(Calendar.MINUTE, -60)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar60MinutesAgo

        val calendar15MinutesAgo = Calendar.getInstance()
        calendar15MinutesAgo.add(Calendar.MINUTE, -15)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar15MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 1.00f

        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(false)
        )
    }

    /**
     * Test a time _after_ the time of the 2nd review (16 minutes).
     */
    @Test
    fun testIsReviewPending_true_17MinutesAfterFirstMastery() {
        val calendar60MinutesAgo = Calendar.getInstance()
        calendar60MinutesAgo.add(Calendar.MINUTE, -60)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar60MinutesAgo

        val calendar17MinutesAgo = Calendar.getInstance()
        calendar17MinutesAgo.add(Calendar.MINUTE, -17)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar17MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 1.00f

        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }

    /**
     * Test a time _before_ the time of the 3rd review (64 minutes).
     */
    @Test
    fun testIsReviewPending_false_63MinutesAfterSecondMastery() {
        val calendar180MinutesAgo = Calendar.getInstance()
        calendar180MinutesAgo.add(Calendar.MINUTE, -180)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar180MinutesAgo

        // 1st review (>=4 minutes after learning event)
        // 180 minutes ago - 120 minutes ago = 60 minutes passed
        val calendar120MinutesAgo = Calendar.getInstance()
        calendar120MinutesAgo.add(Calendar.MINUTE, -120)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar120MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 1.00f

        // 2nd review (>=16 minutes after the 1st mastery)
        // 120 minutes ago - 63 minutes ago = 57
        val calendar63MinutesAgo = Calendar.getInstance()
        calendar63MinutesAgo.add(Calendar.MINUTE, -63)
        val wordAssessmentEventGsonSecond = WordAssessmentEventGson()
        wordAssessmentEventGsonSecond.wordId = 1L
        wordAssessmentEventGsonSecond.timestamp = calendar63MinutesAgo
        wordAssessmentEventGsonSecond.masteryScore = 1.00f

        // Store assessment events in _descending_ order
        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond)
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        // 3rd review (>=64 minutes after the 2nd mastery)
        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(false)
        )
    }

    /**
     * Test a time _after_ the time of the 3rd review (64 minutes).
     */
    @Test
    fun testIsReviewPending_true_65MinutesAfterSecondMastery() {
        val calendar180MinutesAgo = Calendar.getInstance()
        calendar180MinutesAgo.add(Calendar.MINUTE, -180)
        val wordLearningEventGson = WordLearningEventGson()
        wordLearningEventGson.wordId = 1L
        wordLearningEventGson.timestamp = calendar180MinutesAgo

        val calendar120MinutesAgo = Calendar.getInstance()
        calendar120MinutesAgo.add(Calendar.MINUTE, -120)
        val wordAssessmentEventGsonFirst = WordAssessmentEventGson()
        wordAssessmentEventGsonFirst.wordId = 1L
        wordAssessmentEventGsonFirst.timestamp = calendar120MinutesAgo
        wordAssessmentEventGsonFirst.masteryScore = 1.00f

        val calendar65MinutesAgo = Calendar.getInstance()
        calendar65MinutesAgo.add(Calendar.MINUTE, -65)
        val wordAssessmentEventGsonSecond = WordAssessmentEventGson()
        wordAssessmentEventGsonSecond.wordId = 1L
        wordAssessmentEventGsonSecond.timestamp = calendar65MinutesAgo
        wordAssessmentEventGsonSecond.masteryScore = 1.00f

        // Store assessment events in _descending_ order
        val wordAssessmentEventGsons: MutableList<WordAssessmentEventGson> =
            ArrayList<WordAssessmentEventGson>()
        wordAssessmentEventGsons.add(wordAssessmentEventGsonSecond)
        wordAssessmentEventGsons.add(wordAssessmentEventGsonFirst)

        Assert.assertThat<Boolean?>(
            SpacedRepetitionHelper.isReviewPending(
                wordLearningEventGson,
                wordAssessmentEventGsons
            ), CoreMatchers.`is`<Boolean?>(true)
        )
    }
}
