package ai.elimu.kukariri.ui

import ai.elimu.kukariri.LetterSoundItem
import ai.elimu.kukariri.LetterSoundsUiState
import ai.elimu.kukariri.LetterSoundsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * The `LETTER_IDENTIFICATION` skill. The surrounding `Scaffold` is provided by [KukaririApp].
 */
@Composable
fun LetterSoundsScreen(viewModel: LetterSoundsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LetterSoundsContent(uiState = uiState, onRetry = viewModel::load)
}

@Composable
fun LetterSoundsContent(uiState: LetterSoundsUiState, onRetry: () -> Unit) {
    when (uiState) {
        LetterSoundsUiState.Loading -> CenteredMessage("Loading...") {
            CircularProgressIndicator()
        }

        LetterSoundsUiState.ProviderNotInstalled ->
            CenteredMessage("Provider missing", onRetry = onRetry)

        LetterSoundsUiState.Empty ->
            CenteredMessage("Empty", onRetry = onRetry)

        is LetterSoundsUiState.Error ->
            CenteredMessage(uiState.message, onRetry = onRetry)

        is LetterSoundsUiState.Success -> LetterSoundTable(uiState.items)
    }
}

@Composable
private fun LetterSoundTable(items: List<LetterSoundItem>) {
    Column(Modifier.fillMaxSize()) {
        MasteryTableHeader(itemColumnTitle = "Letter-sound")
        HorizontalDivider(thickness = 2.dp)
        LazyColumn(Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                LetterSoundRow(item)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun LetterSoundRow(item: LetterSoundItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "/e/ → e"
        Text(
            text = "/${item.sound}/ → ${item.letters}",
            fontSize = 28.sp,
            modifier = Modifier
                .weight(0.4f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        VerticalDivider()
        MasteryStars(
            stars = item.masteryStars,
            recallProbability = item.recallProbability,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LetterSoundTablePreview() {
    KukaririTheme {
        LetterSoundsContent(
            uiState = LetterSoundsUiState.Success(
                listOf(
                    LetterSoundItem(1, "e", "e", 120, 0.72f, 4),
                    LetterSoundItem(2, "t", "t", 100, 0.10f, 0),
                    LetterSoundItem(3, "a", "a", 90, 0.25f, 1)
                )
            ),
            onRetry = {}
        )
    }
}
