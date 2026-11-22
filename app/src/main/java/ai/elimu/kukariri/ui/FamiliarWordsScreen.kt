package ai.elimu.kukariri.ui

import ai.elimu.kukariri.FamiliarWordItem
import ai.elimu.kukariri.FamiliarWordsUiState
import ai.elimu.kukariri.FamiliarWordsViewModel
import ai.elimu.kukariri.R
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * The `FAMILIAR_WORD_READING` skill: lists each word and the student's mastery of it. The
 * surrounding `Scaffold` is provided by [KukaririApp].
 */
@Composable
fun FamiliarWordsScreen(viewModel: FamiliarWordsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FamiliarWordsContent(uiState = uiState, onRetry = viewModel::load)
}

@Composable
fun FamiliarWordsContent(uiState: FamiliarWordsUiState, onRetry: () -> Unit) {
    when (uiState) {
        FamiliarWordsUiState.Loading -> CenteredMessage("Loading...") {
            CircularProgressIndicator()
        }

        FamiliarWordsUiState.ProviderNotInstalled ->
            CenteredMessage("Provider missing", onRetry = onRetry)

        FamiliarWordsUiState.Empty ->
            CenteredMessage("Empty", onRetry = onRetry)

        is FamiliarWordsUiState.Error ->
            CenteredMessage(uiState.message, onRetry = onRetry)

        is FamiliarWordsUiState.Success -> FamiliarWordTable(uiState.items)
    }
}

@Composable
private fun FamiliarWordTable(items: List<FamiliarWordItem>) {
    Column(Modifier.fillMaxSize()) {
        MasteryTableHeader(itemColumnTitle = stringResource(R.string.column_word))
        HorizontalDivider(thickness = 2.dp)
        LazyColumn(Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                FamiliarWordRow(item)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun FamiliarWordRow(item: FamiliarWordItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.text,
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
private fun FamiliarWordTablePreview() {
    KukaririTheme {
        FamiliarWordsContent(
            uiState = FamiliarWordsUiState.Success(
                listOf(
                    FamiliarWordItem(1, "cat", 120, 0.72f, 4),
                    FamiliarWordItem(2, "sun", 100, 0.10f, 0),
                    FamiliarWordItem(3, "run", 90, 0.25f, 1)
                )
            ),
            onRetry = {}
        )
    }
}
