package ai.elimu.kukariri.assessment

import ai.elimu.analytics.utils.AssessmentEventUtil
import ai.elimu.analytics.utils.EventProviderUtil
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllEmojiGsons
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllWordGsons
import ai.elimu.kukariri.BuildConfig
import ai.elimu.kukariri.R
import ai.elimu.kukariri.logic.ReviewHelper
import ai.elimu.model.v2.enums.content.WordType
import ai.elimu.model.v2.gson.content.WordGson
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WordAssessmentActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null

    private var textView: TextView? = null

    private var difficultButton: Button? = null

    private var easyButton: Button? = null

    /**
     * The student will iterate through this list of Words until they have all been mastered.
     */
    private val wordGsonsPendingReview: MutableList<WordGson> = ArrayList()

    /**
     * Once a Word has been mastered, it's moved from [.wordGsonsPendingReview] to [.wordGsonsMastered].
     */
    private val wordGsonsMastered: MutableList<WordGson> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.name, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_word_assessment)

        progressBar = findViewById(R.id.wordAssessmentProgressBar)

        textView = findViewById(R.id.wordAssessmentTextView)

        difficultButton = findViewById(R.id.wordAssessmentDifficultButton)

        easyButton = findViewById(R.id.wordAssessmentEasyButton)

        // Get a list of the Words that have been previously learned
        val wordLearningEventGsons = EventProviderUtil.getWordLearningEventGsons(
            applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Get a set of the Words that have been previously learned
        val idsOfWordsInWordLearningEvents = EventProviderUtil.getIdsOfWordsInWordLearningEvents(
            applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Get a list of assessment events for the words that have been previously learned
        val wordAssessmentEventGsons = EventProviderUtil.getWordAssessmentEventGsons(
            applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
        )

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        val idsOfWordsPendingReview = ReviewHelper.getIdsOfWordsPendingReview(
            idsOfWordsInWordLearningEvents,
            wordLearningEventGsons,
            wordAssessmentEventGsons
        )
        Log.i(javaClass.name, "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size)

        // Fetch list of Words from the ContentProvider, and exclude those not in the idsOfWordsPendingReview set
        val allWordGsons = getAllWordGsons(
            applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )
        for (wordGson in allWordGsons) {
            if (idsOfWordsPendingReview.contains(wordGson.id)) {
                // Only include adjectives/nouns/verbs
                if ((wordGson.wordType == WordType.ADJECTIVE)
                    || (wordGson.wordType == WordType.NOUN)
                    || (wordGson.wordType == WordType.VERB)
                ) {
                    wordGsonsPendingReview.add(wordGson)
                }
            }
        }
    }

    override fun onStart() {
        Log.i(javaClass.name, "onStart")
        super.onStart()

        loadNextWord()
    }

    private fun loadNextWord() {
        Log.i(javaClass.name, "loadNextWord")

        if (wordGsonsPendingReview.isEmpty()) {
            val intent = Intent(applicationContext, AssessmentCompletedActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Update the progress bar
        val progressPercentage =
            wordGsonsMastered.size * 100 / (wordGsonsPendingReview.size + wordGsonsMastered.size)
        val objectAnimator = ObjectAnimator.ofInt(progressBar, ProgressBar::getProgress.name, progressPercentage)
        objectAnimator.setDuration(1000)
        objectAnimator.start()

        // Display the next Word in the list
        val wordGson = wordGsonsPendingReview[0]
        textView!!.text = wordGson.text
        val appearAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.anim_appear_right)
        textView!!.startAnimation(appearAnimation)

        // Append Emojis (if any) below the Word
        val emojiGsons = getAllEmojiGsons(
            wordGson.id,
            applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )
        if (emojiGsons.isNotEmpty()) {
            textView!!.text = textView!!.text.toString() + "\n"
            for (emojiGson in emojiGsons) {
                textView!!.text = textView!!.text.toString() + emojiGson.glyph
            }
        }

        val timeStart = System.currentTimeMillis()

        difficultButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.i(javaClass.name, "difficultButton onClick")

                // Move the Word to the end of the list
                wordGsonsPendingReview.remove(wordGson)
                wordGsonsPendingReview.add(wordGson)

                // Report assessment event to the Analytics application (https://github.com/elimu-ai/analytics)
                AssessmentEventUtil.reportWordAssessmentEvent(
                    wordGson, 0.00f, System.currentTimeMillis() - timeStart,
                    applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
                )

                loadNextWord()
            }
        })

        easyButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.i(javaClass.name, "easyButton onClick")

                // Remove the Word from the list of Words to be repeated, and add it to the list of mastered Words
                wordGsonsPendingReview.remove(wordGson)
                wordGsonsMastered.add(wordGson)

                // Report assessment event to the Analytics application (https://github.com/elimu-ai/analytics)
                AssessmentEventUtil.reportWordAssessmentEvent(
                    wordGson, 1.00f, System.currentTimeMillis() - timeStart,
                    applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
                )

                loadNextWord()
            }
        })
    }
}
