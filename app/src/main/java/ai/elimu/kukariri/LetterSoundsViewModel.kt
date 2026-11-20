package ai.elimu.kukariri

import ai.elimu.analytics.utils.EventProviderUtil
import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.kukariri.logic.SpacedRepetitionHelper
import ai.elimu.model.v2.gson.content.LetterSoundGson
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** One row in the table: e.g. `/e/ → e` with its mastery. */
data class LetterSoundItem(
    val id: Long,
    /** IPA value(s) of the sound(s), e.g. "e" (shown as "/e/"). */
    val sound: String,
    /** Letter(s) of the letter-sound correspondence, e.g. "e". */
    val letters: String,
    val usageCount: Int,
    /** 0..[MAX_STARS] */
    val masteryStars: Int
)

sealed interface LetterSoundsUiState {
    data object Loading : LetterSoundsUiState
    data object ProviderNotInstalled : LetterSoundsUiState
    data object Empty : LetterSoundsUiState
    data class Error(val message: String) : LetterSoundsUiState
    data class Success(val items: List<LetterSoundItem>) : LetterSoundsUiState
}

const val MAX_STARS = 5

class LetterSoundsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LetterSoundsUiState>(LetterSoundsUiState.Loading)
    val uiState: StateFlow<LetterSoundsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = LetterSoundsUiState.Loading
            _uiState.value = withContext(Dispatchers.IO) { fetchLetterSounds() }
        }
    }

    private fun fetchLetterSounds(): LetterSoundsUiState {
        val context = getApplication<Application>()

        val providerApplicationId = CONTENT_PROVIDER_APPLICATION_IDS.firstOrNull { applicationId ->
            context.packageManager.resolveContentProvider(
                "$applicationId.provider.letter_sound_provider", 0
            ) != null
        } ?: return LetterSoundsUiState.ProviderNotInstalled
        Log.i(TAG, "providerApplicationId: $providerApplicationId")

        return try {
            val items = ContentProviderUtil
                .getAllLetterSoundGsons(context, providerApplicationId)
                .sortedByDescending { it.usageCount ?: 0 } // "Most frequent" first
                .take(MAX_ROWS)
                .map { letterSoundGson ->
                    LetterSoundItem(
                        id = letterSoundGson.id,
                        sound = letterSoundGson.sounds.orEmpty()
                            .joinToString("") { it.valueIpa.orEmpty() },
                        letters = letterSoundGson.letters.orEmpty()
                            .joinToString("") { it.text.orEmpty() },
                        usageCount = letterSoundGson.usageCount ?: 0,
                        masteryStars = masteryStarsFor(letterSoundGson.id, context)
                    )
                }
            if (items.isEmpty()) LetterSoundsUiState.Empty else LetterSoundsUiState.Success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load letter-sounds", e)
            LetterSoundsUiState.Error(e.message ?: e::class.java.simpleName)
        }
    }

    /**
     * Number of filled stars (0..[MAX_STARS]) for a letter-sound.
     */
    private fun masteryStarsFor(letterSoundId: Long, context: Context): Int {
        Log.d(javaClass.name, "masteryStarsFor, letterSoundId: $letterSoundId")

        val letterSoundGson: LetterSoundGson = LetterSoundGson().apply {
            id = letterSoundId
        }
        val letterSoundAssessmentEventGsons = EventProviderUtil.getLetterSoundAssessmentEventGsonsByLetterSound(letterSoundGson, context, BuildConfig.ANALYTICS_APPLICATION_ID)
        Log.d(javaClass.name, "letterSoundAssessmentEventGsons.size: ${letterSoundAssessmentEventGsons.size}")

        val recallProbability: Float = SpacedRepetitionHelper.getRecallProbability(letterSoundAssessmentEventGsons)
        Log.d(javaClass.name, "recallProbability: ${recallProbability}")
        if (recallProbability >= 0.80f) {
            return 5
        } else if (recallProbability >= 0.65f) {
            return 4
        } else if (recallProbability >= 0.50f) {
            return 3
        } else if (recallProbability >= 0.35f) {
            return 2
        } else if (recallProbability >= 0.20f) {
            return 1
        } else {
            return 0
        }
    }

    private companion object {
        const val TAG = "LetterSoundsViewModel"
        const val MAX_ROWS = 30

        /** Release build first, then the debug build of the Content Provider app. */
        val CONTENT_PROVIDER_APPLICATION_IDS = listOf(
            "ai.elimu.content_provider",
            "ai.elimu.content_provider.debug"
        )
    }
}
