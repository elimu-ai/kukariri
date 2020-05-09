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
import java.util.List;

import ai.elimu.kukariri.BuildConfig;
import ai.elimu.kukariri.R;
import ai.elimu.kukariri.util.CursorToWordGsonConverter;
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

        // Fetch list of Words from the ContentProvider
        Uri wordsUri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.word_provider/words");
        Log.i(getClass().getName(), "wordsUri: " + wordsUri);
        Cursor cursor = getContentResolver().query(wordsUri, null, null, null, null);
        if (cursor == null) {
            Log.e(getClass().getName(), "cursor == null");
            Toast.makeText(getApplicationContext(), "cursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "cursor.getCount(): " + cursor.getCount());
            if (cursor.getCount() == 0) {
                Log.e(getClass().getName(), "cursor.getCount() == 0");
            } else {
                Log.i(getClass().getName(), "cursor.getCount(): " + cursor.getCount());

                boolean isLast = false;
                while (!isLast) {
                    cursor.moveToNext();

                    // Convert from Room to Gson
                    WordGson wordGson = CursorToWordGsonConverter.getWordGson(cursor);
                    wordGsons.add(wordGson);

                    // TODO: fetch Words by usageCount (and by wordType), and then remove this code block
                    if (wordGsons.size() == 10) {
                        break;
                    }

                    isLast = cursor.isLast();
                }

                cursor.close();
                Log.i(getClass().getName(), "cursor.isClosed(): " + cursor.isClosed());
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

        difficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().getName(), "difficultButton onClick");

                // Move the Word to the end of the list
                wordGsons.remove(wordGson);
                wordGsons.add(wordGson);

                // Report WordAssessmentEvent to the Analytics application
                // TODO

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
                // TODO

                loadNextWord();
            }
        });
    }
}
