package ai.elimu.kukariri.assessment;

import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private List<WordGson> wordGsons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_assessment);

        progressBar = findViewById(R.id.wordAssessmentProgressBar);

        textView = findViewById(R.id.wordAssessmentTextView);

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

        // Display the first Word in the list
        WordGson wordGson = wordGsons.get(0);
        textView.setText(wordGson.getText());

        loadNextWord();
    }

    private void loadNextWord() {
        Log.i(getClass().getName(), "loadNextWord");

        // Update the progress bar
        int progressPercentage = 20;
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressPercentage);
        objectAnimator.setDuration(1000);
        objectAnimator.start();

        // Animate in the card view containing the word
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_appear_from_right);
        textView.startAnimation(animation);


    }
}
