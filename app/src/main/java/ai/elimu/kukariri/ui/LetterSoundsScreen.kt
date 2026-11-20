package ai.elimu.kukariri.ui

import ai.elimu.kukariri.LetterSoundItem
import ai.elimu.kukariri.LetterSoundsUiState
import ai.elimu.kukariri.LetterSoundsViewModel
import ai.elimu.kukariri.MAX_STARS
import ai.elimu.kukariri.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterSoundsScreen(viewModel: LetterSoundsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name) + " 🧠") }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LetterSoundsContent(uiState = uiState, onRetry = viewModel::load)
        }
    }
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
private fun CenteredMessage(
    message: String,
    onRetry: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        leading?.invoke()
        Text(message, style = MaterialTheme.typography.bodyLarge)
        if (onRetry != null) {
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun LetterSoundTable(items: List<LetterSoundItem>) {
    Column(Modifier.fillMaxSize()) {
        TableHeader()
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
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = "Letter-sound",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(0.4f)
                .padding(16.dp)
        )
        VerticalDivider()
        Text(
            text = "Mastery",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(0.6f)
                .padding(16.dp)
        )
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
        Row(
            modifier = Modifier
                .weight(0.6f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(MAX_STARS) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < item.masteryStars) {
                        Color(0xFFFFB300)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LetterSoundTablePreview() {
    KukaririTheme {
        LetterSoundsContent(
            uiState = LetterSoundsUiState.Success(
                listOf(
                    LetterSoundItem(1, "e", "e", 120, 1),
                    LetterSoundItem(2, "t", "t", 100, 0),
                    LetterSoundItem(3, "a", "a", 90, 1)
                )
            ),
            onRetry = {}
        )
    }
}
