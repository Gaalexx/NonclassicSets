package com.project.nonclassicsets.presentation.workingset.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.presentation.shared.AppMark
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.empty_generate
import nonclassicsets.shared.generated.resources.empty_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptySetCard(
    modifier: Modifier = Modifier,
    onGenerate: () -> Unit,
) {
    FuzzyCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppMark(
                    modifier = Modifier
                        .padding(bottom = 14.dp)
                        .size(72.dp),
                    large = true
                )
                Text(
                    text = stringResource(Res.string.empty_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PrimaryButton(
                    text = stringResource(Res.string.empty_generate),
                    onClick = onGenerate
                )
            }
        }
    }
}
