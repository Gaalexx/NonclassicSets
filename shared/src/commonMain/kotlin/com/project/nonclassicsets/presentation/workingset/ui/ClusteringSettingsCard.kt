package com.project.nonclassicsets.presentation.workingset.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.FuzzyTextField
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import com.project.nonclassicsets.presentation.shared.SegmentedSwitch
import com.project.nonclassicsets.presentation.workingset.state.StopModeType
import com.project.nonclassicsets.presentation.workingset.state.WorkingSetViewModelState
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModelCommand
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModelError
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.alpha_level
import nonclassicsets.shared.generated.resources.by_count
import nonclassicsets.shared.generated.resources.by_threshold
import nonclassicsets.shared.generated.resources.cluster_button
import nonclassicsets.shared.generated.resources.clustering_title
import nonclassicsets.shared.generated.resources.count_field
import nonclassicsets.shared.generated.resources.epsilon_field
import nonclassicsets.shared.generated.resources.error_alpha
import nonclassicsets.shared.generated.resources.error_clustering
import nonclassicsets.shared.generated.resources.error_count
import nonclassicsets.shared.generated.resources.error_cuts
import nonclassicsets.shared.generated.resources.error_epsilon
import nonclassicsets.shared.generated.resources.quantities
import nonclassicsets.shared.generated.resources.stop_merging
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClusteringSettingsCard(
    modifier: Modifier = Modifier,
    state: WorkingSetViewModelState,
    onEvent: (WorkingSetViewModelCommand) -> Unit,
) {
    val isEmpty = state.quantities.isEmpty()
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    FuzzyCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(Res.string.clustering_title),
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )

            FuzzyTextField(
                value = state.alphaInput,
                onValueChange = { onEvent(WorkingSetViewModelCommand.OnAlphaChange(it)) },
                label = stringResource(Res.string.alpha_level),
                error = when {
                    state.alphaError -> stringResource(Res.string.error_alpha)
                    state.error is WorkingSetViewModelError.CutsFailed -> stringResource(Res.string.error_cuts)
                    else -> null
                }
            )

            if (!isEmpty) {
                Text(
                    text = stringResource(Res.string.stop_merging),
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }

            SegmentedSwitch(
                options = listOf(
                    stringResource(Res.string.by_threshold),
                    stringResource(Res.string.by_count)
                ),
                selectedIndex = state.stopModeType.ordinal,
                onSelect = { onEvent(WorkingSetViewModelCommand.OnStopModeChange(StopModeType.entries[it])) }
            )

            when (state.stopModeType) {
                StopModeType.Threshold -> FuzzyTextField(
                    value = state.epsilonInput,
                    onValueChange = { onEvent(WorkingSetViewModelCommand.OnEpsilonChange(it)) },
                    label = stringResource(Res.string.epsilon_field),
                    error = if (state.epsilonError) stringResource(Res.string.error_epsilon) else null
                )

                StopModeType.Count -> FuzzyTextField(
                    value = state.countInput,
                    onValueChange = { onEvent(WorkingSetViewModelCommand.OnCountChange(it)) },
                    label = stringResource(Res.string.count_field),
                    error = if (state.countError) {
                        stringResource(Res.string.error_count, state.quantities.size.coerceAtLeast(1))
                    } else {
                        null
                    }
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(Res.string.cluster_button),
                enabled = !isEmpty && !state.isCalculating,
                onClick = { onEvent(WorkingSetViewModelCommand.Cluster) }
            )

            if (state.error is WorkingSetViewModelError.ClusteringFailed) {
                Text(
                    text = stringResource(Res.string.error_clustering),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
