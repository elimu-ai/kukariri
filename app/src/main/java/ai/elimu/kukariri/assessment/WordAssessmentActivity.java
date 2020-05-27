package ai.elimu.kukariri.assessment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ai.elimu.kukariri.BuildConfig;
import ai.elimu.kukariri.R;
import ai.elimu.kukariri.logic.SpacedRepetitionHelper;
import ai.elimu.kukariri.util.CursorToEmojiGsonConverter;
import ai.elimu.kukariri.util.CursorToWordAssessmentEventGsonConverter;
import ai.elimu.kukariri.util.CursorToWordGsonConverter;
import ai.elimu.kukariri.util.CursorToWordLearningEventGsonConverter;
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
    private List<WordGson> wordGsons = new ArrayList<>();

    /**
     * Once a Word has been mastered, it's moved from {@link #wordGsons} to {@link #wordGsonsMastered}.
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

        // Get a set of the Words that have been previously learned
        List<WordLearningEventGson> wordLearningEventGsons = getWordLearningEventGsons();

        // Get a set of the Words that have been previously learned
        Set<Long> idsOfWordsInWordLearningEvents = getIdsOfWordsInWordLearningEvents();

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        Set<Long> idsOfWordsPendingReview = new HashSet<>();
        List<WordAssessmentEventGson> wordAssessmentEventGsons = getWordAssessmentEventGsons(idsOfWordsInWordLearningEvents);
        for (Long idOfWordInWordLearningEvent : idsOfWordsInWordLearningEvents) {
            WordLearningEventGson originalWordLearningEventGson = null;
            for (WordLearningEventGson wordLearningEventGson : wordLearningEventGsons) {
                if (wordLearningEventGson.getWordId().equals(idOfWordInWordLearningEvent)) {
                    originalWordLearningEventGson = wordLearningEventGson;
                    break;
                }
            }

            List<WordAssessmentEventGson> wordAssessmentEventGsonsAssociatedWithLearningEvent = new ArrayList<>();
            for (WordAssessmentEventGson wordAssessmentEventGson : wordAssessmentEventGsons) {
                if (wordAssessmentEventGson.getWordId().equals(idOfWordInWordLearningEvent)) {
                    wordAssessmentEventGsonsAssociatedWithLearningEvent.add(wordAssessmentEventGson);
                }
            }

            // Check if the Word has any pending assessment reviews
            boolean isReviewPending = SpacedRepetitionHelper.isReviewPending(originalWordLearningEventGson, wordAssessmentEventGsonsAssociatedWithLearningEvent);
            if (isReviewPending) {
                idsOfWordsPendingReview.add(idOfWordInWordLearningEvent);
            }
        }
        Log.i(getClass().getName(), "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size());

        // Fetch list of Words from the ContentProvider, and exclude those not in the idsOfWordsPendingReview set
        Uri wordsUri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.word_provider/words");
        Log.i(getClass().getName(), "wordsUri: " + wordsUri);
        Cursor wordsCursor = getContentResolver().query(wordsUri, null, null, null, null);
        if (wordsCursor == null) {
            Log.e(getClass().getName(), "wordsCursor == null");
            Toast.makeText(getApplicationContext(), "wordsCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "wordsCursor.getCount(): " + wordsCursor.getCount());
            if (wordsCursor.getCount() == 0) {
                Log.e(getClass().getName(), "wordsCursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    wordsCursor.moveToNext();

                    // Convert from Room to Gson
                    WordGson wordGson = CursorToWordGsonConverter.getWordGson(wordsCursor);

                    if (idsOfWordsPendingReview.contains(wordGson.getId())) {
                        // Only include adjectives/nouns/verbs
                        if (       (wordGson.getWordType() == WordType.ADJECTIVE)
                                || (wordGson.getWordType() == WordType.NOUN)
                                || (wordGson.getWordType() == WordType.VERB)
                        ) {
                            wordGsons.add(wordGson);
                        }
                    }

                    isLast = wordsCursor.isLast();
                }

                wordsCursor.close();
                Log.i(getClass().getName(), "wordsCursor.isClosed(): " + wordsCursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "wordGsons: " + wordGsons);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        loadNextWord();
    }

    private List<WordLearningEventGson> getWordLearningEventGsons() {
        Log.i(getClass().getName(), "getWordLearningEventGsons");

        List<WordLearningEventGson> wordLearningEventGsons = new ArrayList<>();

        // Fetch list of WordLearningEvents from the Analytics application
        Uri wordLearningEventsUri = Uri.parse("content://" + BuildConfig.ANALYTICS_APPLICATION_ID + ".provider.word_learning_event_provider/events");
        Log.i(getClass().getName(), "wordLearningEventsUri: " + wordLearningEventsUri);
        Cursor wordLearningEventsCursor = getContentResolver().query(wordLearningEventsUri, null, null, null, null);
        Log.i(getClass().getName(), "wordLearningEventsCursor: " + wordLearningEventsCursor);
        if (wordLearningEventsCursor == null) {
            Log.e(getClass().getName(), "wordLearningEventsCursor == null");
            Toast.makeText(getApplicationContext(), "wordLearningEventsCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "wordLearningEventsCursor.getCount(): " + wordLearningEventsCursor.getCount());
            if (wordLearningEventsCursor.getCount() == 0) {
                Log.e(getClass().getName(), "wordLearningEventsCursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    wordLearningEventsCursor.moveToNext();

                    // Convert from Room to Gson
                    WordLearningEventGson wordLearningEventGson = CursorToWordLearningEventGsonConverter.getWordLearningEventGson(wordLearningEventsCursor);

                    wordLearningEventGsons.add(wordLearningEventGson);

                    isLast = wordLearningEventsCursor.isLast();
                }

                wordLearningEventsCursor.close();
                Log.i(getClass().getName(), "wordLearningEventsCursor.isClosed(): " + wordLearningEventsCursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "wordLearningEventGsons.size(): " + wordLearningEventGsons.size());

        return wordLearningEventGsons;
    }

    private Set<Long> getIdsOfWordsInWordLearningEvents() {
        Log.i(getClass().getName(), "getIdsOfWordsInWordLearningEvents");

        Set<Long> wordIdsSet = new HashSet<>();

        // Fetch list of WordLearningEvents from the Analytics application
        Uri wordLearningEventsUri = Uri.parse("content://" + BuildConfig.ANALYTICS_APPLICATION_ID + ".provider.word_learning_event_provider/events");
        Log.i(getClass().getName(), "wordLearningEventsUri: " + wordLearningEventsUri);
        Cursor wordLearningEventsCursor = getContentResolver().query(wordLearningEventsUri, null, null, null, null);
        Log.i(getClass().getName(), "wordLearningEventsCursor: " + wordLearningEventsCursor);
        if (wordLearningEventsCursor == null) {
            Log.e(getClass().getName(), "wordLearningEventsCursor == null");
            Toast.makeText(getApplicationContext(), "wordLearningEventsCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "wordLearningEventsCursor.getCount(): " + wordLearningEventsCursor.getCount());
            if (wordLearningEventsCursor.getCount() == 0) {
                Log.e(getClass().getName(), "wordLearningEventsCursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    wordLearningEventsCursor.moveToNext();

                    // Convert from Room to Gson
                    WordLearningEventGson wordLearningEventGson = CursorToWordLearningEventGsonConverter.getWordLearningEventGson(wordLearningEventsCursor);

                    wordIdsSet.add(wordLearningEventGson.getWordId());

                    isLast = wordLearningEventsCursor.isLast();
                }

                wordLearningEventsCursor.close();
                Log.i(getClass().getName(), "wordLearningEventsCursor.isClosed(): " + wordLearningEventsCursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "wordIdsSet.size(): " + wordIdsSet.size());

        return wordIdsSet;
    }
    
    private List<WordAssessmentEventGson> getWordAssessmentEventGsons(Set<Long> idsOfWordsInWordLearningEvents) {
        Log.i(getClass().getName(), "getWordAssessmentEventGsons");

        List<WordAssessmentEventGson> wordAssessmentEventGsons = new ArrayList<>();

        // Fetch list of WordAssessmentEvents from the Analytics application
        Uri wordAssessmentEventsUri = Uri.parse("content://" + BuildConfig.ANALYTICS_APPLICATION_ID + ".provider.word_assessment_event_provider/events");
        Log.i(getClass().getName(), "wordAssessmentEventsUri: " + wordAssessmentEventsUri);
        Cursor wordAssessmentEventsCursor = getContentResolver().query(wordAssessmentEventsUri, null, null, null, null);
        Log.i(getClass().getName(), "wordAssessmentEventsCursor: " + wordAssessmentEventsCursor);
        if (wordAssessmentEventsCursor == null) {
            Log.e(getClass().getName(), "wordAssessmentEventsCursor == null");
            Toast.makeText(getApplicationContext(), "wordAssessmentEventsCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "wordAssessmentEventsCursor.getCount(): " + wordAssessmentEventsCursor.getCount());
            if (wordAssessmentEventsCursor.getCount() == 0) {
                Log.e(getClass().getName(), "wordAssessmentEventsCursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    wordAssessmentEventsCursor.moveToNext();

                    // Convert from Room to Gson
                    WordAssessmentEventGson wordAssessmentEventGson = CursorToWordAssessmentEventGsonConverter.getWordAssessmentEventGson(wordAssessmentEventsCursor);

                    wordAssessmentEventGsons.add(wordAssessmentEventGson);

                    isLast = wordAssessmentEventsCursor.isLast();
                }

                wordAssessmentEventsCursor.close();
                Log.i(getClass().getName(), "wordAssessmentEventsCursor.isClosed(): " + wordAssessmentEventsCursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "wordAssessmentEventGsons.size(): " + wordAssessmentEventGsons.size());
        
        return wordAssessmentEventGsons;
    }

    private List<EmojiGson> getEmojiGsons(Long wordId) {
        Log.i(getClass().getName(), "getEmojiGsons");

        List<EmojiGson> emojiGsons = new ArrayList<>();

        // Fetch list of WordAssessmentEvents from the Analytics application
        Uri emojisUri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.emoji_provider/emojis/by-word-label-id/" + wordId);
        Log.i(getClass().getName(), "emojisUri: " + emojisUri);
        Cursor emojisCursor = getContentResolver().query(emojisUri, null, null, null, null);
        Log.i(getClass().getName(), "emojisCursor: " + emojisCursor);
        if (emojisCursor == null) {
            Log.e(getClass().getName(), "emojisCursor == null");
            Toast.makeText(getApplicationContext(), "emojisCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "emojisCursor.getCount(): " + emojisCursor.getCount());
            if (emojisCursor.getCount() == 0) {
                Log.e(getClass().getName(), "emojisCursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    emojisCursor.moveToNext();

                    // Convert from Room to Gson
                    EmojiGson emojiGson = CursorToEmojiGsonConverter.getEmojiGson(emojisCursor);

                    emojiGsons.add(emojiGson);

                    isLast = emojisCursor.isLast();
                }

                emojisCursor.close();
                Log.i(getClass().getName(), "emojisCursor.isClosed(): " + emojisCursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "emojiGsons.size(): " + emojiGsons.size());

        return emojiGsons;
    }

    private void loadNextWord() {
        Log.i(getClass().getName(), "loadNextWord");

        if (wordGsons.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), AssessmentCompletedActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Update the progress bar
        int progressPercentage = wordGsonsMastered.size() * 100 / (wordGsons.size() + wordGsonsMastered.size());
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressPercentage);
        objectAnimator.setDuration(1000);
        objectAnimator.start();

        // Display the next Word in the list
        final WordGson wordGson = wordGsons.get(0);
        textView.setText(wordGson.getText());
        Animation appearAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_appear_right);
        textView.startAnimation(appearAnimation);

        // Append Emojis (if any) below the Word
        List<EmojiGson> emojiGsons = getEmojiGsons(wordGson.getId());
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
                wordGsons.remove(wordGson);
                wordGsons.add(wordGson);

                // Report WordAssessmentEvent to the Analytics application
                Intent broadcastIntent = new Intent();
                broadcastIntent.setPackage(BuildConfig.ANALYTICS_APPLICATION_ID);
                broadcastIntent.setAction("ai.elimu.intent.action.WORD_ASSESSMENT_EVENT");
                broadcastIntent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                broadcastIntent.putExtra("wordId", wordGson.getId());
                broadcastIntent.putExtra("wordText", wordGson.getText());
                broadcastIntent.putExtra("masteryScore", 0.00f);
                broadcastIntent.putExtra("timeSpentMs", System.currentTimeMillis() - timeStart);
                sendBroadcast(broadcastIntent);

                loadNextWord();
            }
        });

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().getName(), "easyButton onClick");

                // Remove the Word from the list of Words to be repeated, and add it to the list of mastered Words
                wordGsons.remove(wordGson);
                wordGsonsMastered.add(wordGson);

                // Report WordAssessmentEvent to the Analytics application
                Intent broadcastIntent = new Intent();
                broadcastIntent.setPackage(BuildConfig.ANALYTICS_APPLICATION_ID);
                broadcastIntent.setAction("ai.elimu.intent.action.WORD_ASSESSMENT_EVENT");
                broadcastIntent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                broadcastIntent.putExtra("wordId", wordGson.getId());
                broadcastIntent.putExtra("wordText", wordGson.getText());
                broadcastIntent.putExtra("masteryScore", 1.00f);
                broadcastIntent.putExtra("timeSpentMs", System.currentTimeMillis() - timeStart);
                sendBroadcast(broadcastIntent);

                loadNextWord();
            }
        });
    }
}
