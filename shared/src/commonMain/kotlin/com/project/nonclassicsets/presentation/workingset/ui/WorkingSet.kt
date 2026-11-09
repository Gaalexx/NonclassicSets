package com.project.nonclassicsets.presentation.workingset.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.shared.TintedBlock
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import com.project.nonclassicsets.presentation.workingset.state.WorkingSetViewModelState
import com.project.nonclassicsets.presentation.workingset.viewmodel.ImportNotice
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetEvent
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModel
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModelCommand
import dev.zacsweers.metrox.viewmodel.metroViewModel
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.add_button
import nonclassicsets.shared.generated.resources.generate_button
import nonclassicsets.shared.generated.resources.import_button
import nonclassicsets.shared.generated.resources.import_empty_file
import nonclassicsets.shared.generated.resources.import_notice_hide
import nonclassicsets.shared.generated.resources.import_read_error
import nonclassicsets.shared.generated.resources.import_some_invalid
import nonclassicsets.shared.generated.resources.quantities
import nonclassicsets.shared.generated.resources.working_set_title
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WorkingSetRoot(
    navigateToGenerate: () -> Unit = {},
    navigateToAdd: () -> Unit = {},
    navigateToResult: () -> Unit = {},
    viewModel: WorkingSetViewModel = metroViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val onEvent = viewModel::onEvent

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is WorkingSetEvent.NavigateToResult -> navigateToResult()
            }
        }
    }

    WorkingSet(
        state = state,
        onEvent = onEvent,
        navigateToGenerate = navigateToGenerate,
        navigateToAdd = navigateToAdd
    )
}

@Composable
fun WorkingSet(
    state: WorkingSetViewModelState,
    onEvent: (WorkingSetViewModelCommand) -> Unit = {},
    navigateToGenerate: () -> Unit = {},
    navigateToAdd: () -> Unit = {},
) {
    val isEmpty = state.quantities.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.working_set_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                SecondaryButton(
                    text = stringResource(Res.string.import_button),
                    enabled = !state.isImporting,
                    onClick = { onEvent(WorkingSetViewModelCommand.ImportFile) }
                )
                SecondaryButton(text = stringResource(Res.string.generate_button), onClick = navigateToGenerate)
                PrimaryButton(text = stringResource(Res.string.add_button), onClick = navigateToAdd)
            }

            state.importNotice?.let { notice ->
                ImportNoticeBlock(
                    notice = notice,
                    onDismiss = { onEvent(WorkingSetViewModelCommand.DismissImportNotice) }
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (isEmpty) {
                    EmptySetCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onGenerate = navigateToGenerate
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        QuantitiesTableCard(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            state = state,
                            onEvent = onEvent
                        )
                        SelectedQuantityCard(
                            modifier = Modifier.fillMaxWidth(),
                            state = state
                        )
                    }
                }

                ClusteringSettingsCard(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun ImportNoticeBlock(
    notice: ImportNotice,
    onDismiss: () -> Unit,
) {
    TintedBlock(
        modifier = Modifier.fillMaxWidth(),
        container = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = when (notice) {
                    is ImportNotice.EmptyFile -> stringResource(Res.string.import_empty_file)
                    is ImportNotice.ReadFailed -> stringResource(Res.string.import_read_error)
                    is ImportNotice.RowsSkipped -> stringResource(
                        Res.string.import_some_invalid,
                        notice.skipped,
                        pluralStringResource(Res.plurals.quantities, notice.added, notice.added)
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.width(16.dp))
            SecondaryButton(text = stringResource(Res.string.import_notice_hide), onClick = onDismiss)
        }
    }
}

private val previewQuantities = listOf(
    FuzzyQuantity("A", "Величина A", 6.0, 8.0),
    FuzzyQuantity("B", "Величина B", 6.2, 8.0),
    FuzzyQuantity("C", "Величина C", 12.0, 8.0),
    FuzzyQuantity("D", "Величина D", 22.0, 8.0),
)

@Preview
@Composable
private fun WorkingSetPreview() {
    FuzzyLabTheme {
        WorkingSet(
            state = WorkingSetViewModelState(
                quantities = previewQuantities,
                cuts = mapOf(
                    "A" to QuantityWithCut(previewQuantities[0], 0.4, AlphaCut(0.161, 0.897)),
                    "B" to QuantityWithCut(previewQuantities[1], 0.42, AlphaCut(0.172, 0.931)),
                    "C" to QuantityWithCut(previewQuantities[2], 1.0, AlphaCut(0.525, 1.905)),
                    "D" to QuantityWithCut(previewQuantities[3], 2.0, AlphaCut(1.165, 3.552)),
                ),
                selectedId = "A"
            )
        )
    }
}

@Preview
@Composable
private fun WorkingSetEmptyPreview() {
    FuzzyLabTheme {
        WorkingSet(state = WorkingSetViewModelState())
    }
}
