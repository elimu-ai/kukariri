package ai.elimu.kukariri

import ai.elimu.analytics.utils.EventProviderUtil
import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.kukariri.logic.SpacedRepetitionHelper
import ai.elimu.model.v2.enums.content.WordType
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** One row in the table: e.g. `cat` with its mastery. */
data class FamiliarWordItem(
    val id: Long,
    val text: String,
    val usageCount: Int,
    /** Probability (0.0..1.0) that the student can still recall (read) the word. */
    val recallProbability: Float,
    /** 0..[MAX_STARS] */
    val masteryStars: Int
)

sealed interface FamiliarWordsUiState {
    data object Loading : FamiliarWordsUiState
    data object ProviderNotInstalled : FamiliarWordsUiState
    data object Empty : FamiliarWordsUiState
    data class Error(val message: String) : FamiliarWordsUiState
    data class Success(val items: List<FamiliarWordItem>) : FamiliarWordsUiState
}

class FamiliarWordsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<FamiliarWordsUiState>(FamiliarWordsUiState.Loading)
    val uiState: StateFlow<FamiliarWordsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    /**
     * (Re)loads the words and their masteries. Called from `MainActivity.onStart`, so the
     * masteries are refreshed every time the student returns to the app.
     */
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = FamiliarWordsUiState.Loading
            _uiState.value = withContext(Dispatchers.IO) { fetchWords() }
        }
    }

    private fun fetchWords(): FamiliarWordsUiState {
        val context = getApplication<Application>()

        val providerApplicationId = CONTENT_PROVIDER_APPLICATION_IDS.firstOrNull { applicationId ->
            context.packageManager.resolveContentProvider(
                "$applicationId.provider.word_provider", 0
            ) != null
        } ?: return FamiliarWordsUiState.ProviderNotInstalled
        Log.i(TAG, "providerApplicationId: $providerApplicationId")

        return try {
            // Fetch all word assessment events with a single query, and group them by word
            val wordAssessmentEventGsonsByWordId = EventProviderUtil
                .getWordAssessmentEventGsons(
                    context = context,
                    analyticsApplicationId = BuildConfig.ANALYTICS_APPLICATION_ID
                )
                .groupBy { it.wordId }
            Log.i(TAG, "wordAssessmentEventGsonsByWordId.size: ${wordAssessmentEventGsonsByWordId.size}")

            val items = ContentProviderUtil
                .getAllWordGsons(
                    context = context,
                    contentProviderApplicationId = providerApplicationId
                )
                // Only include the word types that can be assessed (see `WordAssessmentActivity`)
                .filter { it.wordType in ASSESSED_WORD_TYPES }
                .sortedByDescending { it.usageCount ?: 0 } // "Most frequent" first
                .take(MAX_ROWS)
                .map { wordGson ->
                    // `SpacedRepetitionHelper` expects the most recent events first
                    val wordAssessmentEventGsons = wordAssessmentEventGsonsByWordId[wordGson.id]
                        .orEmpty()
                        .sortedByDescending { it.timestamp.timeInMillis }
                    val recallProbability =
                        SpacedRepetitionHelper.getRecallProbability(wordAssessmentEventGsons)
                    FamiliarWordItem(
                        id = wordGson.id,
                        text = wordGson.text.orEmpty(),
                        usageCount = wordGson.usageCount ?: 0,
                        recallProbability = recallProbability,
                        masteryStars = starsFor(recallProbability)
                    )
                }
            if (items.isEmpty()) FamiliarWordsUiState.Empty else FamiliarWordsUiState.Success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load words", e)
            FamiliarWordsUiState.Error(e.message ?: e::class.java.simpleName)
        }
    }

    private companion object {
        const val TAG = "FamiliarWordsViewModel"
        const val MAX_ROWS = 30

        val ASSESSED_WORD_TYPES = setOf(WordType.ADJECTIVE, WordType.NOUN, WordType.VERB)
    }
}
