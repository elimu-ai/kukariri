package ai.elimu.kukariri.assessment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ai.elimu.analytics.utils.AssessmentEventUtil;
import ai.elimu.analytics.utils.ContentProviderUtil;
import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.kukariri.BuildConfig;
import ai.elimu.kukariri.R;
import ai.elimu.kukariri.util.ReviewHelper;
import ai.elimu.model.enums.content.WordType;
import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;
import ai.elimu.model.v2.gson.content.EmojiGson;
import ai.elimu.model.v2.gson.content.WordGson;

public class WordAssessmentActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private TextView textView;

    private Button difficultButton;

    private Button easyButton;

    /**
     * The student will iterate through this list of Words until they have all been mastered.
     */
    private List<WordGson> wordGsonsPendingReview = new ArrayList<>();

    /**
     * Once a Word has been mastered, it's moved from {@link #wordGsonsPendingReview} to {@link #wordGsonsMastered}.
     */
    private List<WordGson> wordGsonsMastered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_assessment);

        progressBar = findViewById(R.id.wordAssessmentProgressBar);

        textView = findViewById(R.id.wordAssessmentTextView);

        difficultButton = findViewById(R.id.wordAssessmentDifficultButton);

        easyButton = findViewById(R.id.wordAssessmentEasyButton);

        // Get a list of the Words that have been previously learned
        List<WordLearningEventGson> wordLearningEventGsons = ContentProviderUtil.getWordLearningEventGsons(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);

        // Get a set of the Words that have been previously learned
        Set<Long> idsOfWordsInWordLearningEvents = ContentProviderUtil.getIdsOfWordsInWordLearningEvents(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);

        // Get a list of assessment events for the words that have been previously learned
        List<WordAssessmentEventGson> wordAssessmentEventGsons = ContentProviderUtil.getWordAssessmentEventGsons(idsOfWordsInWordLearningEvents, getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        Set<Long> idsOfWordsPendingReview = ReviewHelper.getIdsOfWordsPendingReview(idsOfWordsInWordLearningEvents, wordLearningEventGsons, wordAssessmentEventGsons);
        Log.i(getClass().getName(), "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size());

        // Fetch list of Words from the ContentProvider, and exclude those not in the idsOfWordsPendingReview set
        List<WordGson> allWordGsons = ContentProviderHelper.getWordGsons(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        for (WordGson wordGson : allWordGsons) {
            if (idsOfWordsPendingReview.contains(wordGson.getId())) {
                // Only include adjectives/nouns/verbs
                if (       (wordGson.getWordType() == WordType.ADJECTIVE)
                        || (wordGson.getWordType() == WordType.NOUN)
                        || (wordGson.getWordType() == WordType.VERB)
                ) {
                    wordGsonsPendingReview.add(wordGson);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        loadNextWord();
    }

    private void loadNextWord() {
        Log.i(getClass().getName(), "loadNextWord");

        if (wordGsonsPendingReview.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), AssessmentCompletedActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Update the progress bar
        int progressPercentage = wordGsonsMastered.size() * 100 / (wordGsonsPendingReview.size() + wordGsonsMastered.size());
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressPercentage);
        objectAnimator.setDuration(1000);
        objectAnimator.start();

        // Display the next Word in the list
        final WordGson wordGson = wordGsonsPendingReview.get(0);
        textView.setText(wordGson.getText());
        Animation appearAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_appear_right);
        textView.startAnimation(appearAnimation);

        // Append Emojis (if any) below the Word
        List<EmojiGson> emojiGsons = ContentProviderHelper.getEmojiGsons(wordGson.getId(), getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        if (!emojiGsons.isEmpty()) {
            textView.setText(textView.getText() + "\n");
            for (EmojiGson emojiGson : emojiGsons) {
                textView.setText(textView.getText() + emojiGson.getGlyph());
            }
        }

        final long timeStart = System.currentTimeMillis();

        difficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().getName(), "difficultButton onClick");

                // Move the Word to the end of the list
                wordGsonsPendingReview.remove(wordGson);
                wordGsonsPendingReview.add(wordGson);

                // Report assessment event to the Analytics application (https://github.com/elimu-ai/analytics)
                AssessmentEventUtil.reportWordAssessmentEvent(BuildConfig.APPLICATION_ID, wordGson, 0.00f, System.currentTimeMillis() - timeStart, getApplicationContext(), BuildConfig.ANALYTICS_APPLICATION_ID);

                loadNextWord();
            }
        });

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().getName(), "easyButton onClick");

                // Remove the Word from the list of Words to be repeated, and add it to the list of mastered Words
                wordGsonsPendingReview.remove(wordGson);
                wordGsonsMastered.add(wordGson);

                // Report assessment event to the Analytics application (https://github.com/elimu-ai/analytics)
                AssessmentEventUtil.reportWordAssessmentEvent(BuildConfig.APPLICATION_ID, wordGson, 1.00f, System.currentTimeMillis() - timeStart, getApplicationContext(), BuildConfig.ANALYTICS_APPLICATION_ID);

                loadNextWord();
            }
        });
    }
}
