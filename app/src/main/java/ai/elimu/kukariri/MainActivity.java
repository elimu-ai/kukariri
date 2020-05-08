package ai.elimu.kukariri;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.elimu.kukariri.util.CursorToWordGsonConverter;
import ai.elimu.model.v2.gson.content.WordGson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Fetch list of Words from the ContentProvider
        List<WordGson> wordGsons = null;
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

                wordGsons = new ArrayList<>();

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
}
