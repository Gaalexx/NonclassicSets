package com.project.nonclassicsets.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.app_mark
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppMark(
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (large) 20.dp else 9.dp))
            .background(
                if (large) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.app_mark),
            style = if (large) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleMedium,
            color = if (large) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
        )
    }
}
