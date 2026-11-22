package ai.elimu.kukariri.ui

import ai.elimu.kukariri.MAX_STARS
import ai.elimu.kukariri.R
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Components shared by the literacy skill screens (letter identification, familiar word reading).
 */

@Composable
internal fun CenteredMessage(
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

/** Header row of a two-column table: the item (e.g. a word) and its mastery. */
@Composable
internal fun MasteryTableHeader(itemColumnTitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = itemColumnTitle,
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

/**
 * [MAX_STARS] stars, of which [stars] are filled. Shows the recall probability when pressed.
 */
@Composable
internal fun MasteryStars(
    stars: Int,
    recallProbability: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            // Show the recall probability when the stars container is pressed
            .clickable {
                val percent = (recallProbability * 100).roundToInt()
                Toast.makeText(
                    context,
                    context.getString(R.string.recall_probability_toast, percent),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(MAX_STARS) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < stars) {
                    Color(0xFFFFB300)
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
