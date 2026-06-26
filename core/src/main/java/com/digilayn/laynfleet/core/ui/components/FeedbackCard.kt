package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class FeedbackTone { POSITIVE, NEGATIVE, NEUTRAL }

@Composable
fun FeedbackCard(
    title: String,
    message: String,
    tone: FeedbackTone,
    modifier: Modifier = Modifier,
) {
    val colors = feedbackColors(tone)
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = colors.icon,
                contentDescription = null,
                tint = colors.content,
                modifier = Modifier.padding(top = 2.dp),
            )
            Column(Modifier.padding(start = 12.dp)) {
                Text(title, color = colors.content, fontWeight = FontWeight.Bold)
                Text(
                    message,
                    color = colors.content,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}

private data class FeedbackColors(
    val container: Color,
    val content: Color,
    val icon: ImageVector,
)

@Composable
private fun feedbackColors(tone: FeedbackTone): FeedbackColors = when (tone) {
    FeedbackTone.POSITIVE -> FeedbackColors(
        container = MaterialTheme.colorScheme.primaryContainer,
        content = MaterialTheme.colorScheme.onPrimaryContainer,
        icon = Icons.Default.CheckCircle,
    )
    FeedbackTone.NEGATIVE -> FeedbackColors(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
        icon = Icons.Default.Error,
    )
    FeedbackTone.NEUTRAL -> FeedbackColors(
        container = MaterialTheme.colorScheme.surfaceContainerHighest,
        content = MaterialTheme.colorScheme.onSurfaceVariant,
        icon = Icons.Default.Info,
    )
}
