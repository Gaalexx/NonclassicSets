package com.project.nonclassicsets.presentation.generatedialog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.nonclassicsets.presentation.generatedialog.state.GenerateDialogViewModelState
import com.project.nonclassicsets.presentation.generatedialog.viewmodel.GenerateDialogEvent
import com.project.nonclassicsets.presentation.generatedialog.viewmodel.GenerateDialogViewModel
import com.project.nonclassicsets.presentation.generatedialog.viewmodel.GenerateDialogViewModelCommand
import com.project.nonclassicsets.presentation.shared.FuzzyDialog
import com.project.nonclassicsets.presentation.shared.FuzzyTextField
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import com.project.nonclassicsets.presentation.shared.RadioOption
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.shared.TintedBlock
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.append_to_set
import nonclassicsets.shared.generated.resources.cancel
import nonclassicsets.shared.generated.resources.error_k1_from
import nonclassicsets.shared.generated.resources.error_k2_from
import nonclassicsets.shared.generated.resources.error_positive_int
import nonclassicsets.shared.generated.resources.error_seed
import nonclassicsets.shared.generated.resources.error_until
import nonclassicsets.shared.generated.resources.generate_button
import nonclassicsets.shared.generated.resources.generate_count
import nonclassicsets.shared.generated.resources.generate_ranges
import nonclassicsets.shared.generated.resources.generate_title
import nonclassicsets.shared.generated.resources.k1_from
import nonclassicsets.shared.generated.resources.k1_until
import nonclassicsets.shared.generated.resources.k2_from
import nonclassicsets.shared.generated.resources.k2_until
import nonclassicsets.shared.generated.resources.replace_set
import nonclassicsets.shared.generated.resources.seed
import nonclassicsets.shared.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun GenerateDialogRoot(
    onDismiss: () -> Unit = {},
    viewModel: GenerateDialogViewModel = metroViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val onEvent = viewModel::onEvent

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is GenerateDialogEvent.Dismiss -> onDismiss()
            }
        }
    }

    GenerateDialog(state = state, onEvent = onEvent, onDismiss = onDismiss)
}

@Composable
fun GenerateDialog(
    state: GenerateDialogViewModelState,
    onEvent: (GenerateDialogViewModelCommand) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    FuzzyDialog(
        width = 560.dp,
        title = stringResource(Res.string.generate_title),
        actions = {
            SecondaryButton(text = stringResource(Res.string.cancel), onClick = onDismiss)
            PrimaryButton(
                text = stringResource(Res.string.generate_button),
                enabled = !state.hasErrors && !state.isGenerating,
                onClick = { onEvent(GenerateDialogViewModelCommand.Generate) }
            )
        }
    ) {
        FuzzyTextField(
            value = state.countInput,
            onValueChange = { onEvent(GenerateDialogViewModelCommand.OnCountChange(it)) },
            label = stringResource(Res.string.generate_count),
            error = if (state.countError) stringResource(Res.string.error_positive_int) else null
        )

        Text(
            text = stringResource(Res.string.generate_ranges),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k1FromInput,
                onValueChange = { onEvent(GenerateDialogViewModelCommand.OnK1FromChange(it)) },
                label = stringResource(Res.string.k1_from),
                error = if (state.k1FromError) stringResource(Res.string.error_k1_from) else null
            )
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k1UntilInput,
                onValueChange = { onEvent(GenerateDialogViewModelCommand.OnK1UntilChange(it)) },
                label = stringResource(Res.string.k1_until),
                error = if (state.k1UntilError) stringResource(Res.string.error_until) else null
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k2FromInput,
                onValueChange = { onEvent(GenerateDialogViewModelCommand.OnK2FromChange(it)) },
                label = stringResource(Res.string.k2_from),
                error = if (state.k2FromError) stringResource(Res.string.error_k2_from) else null
            )
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k2UntilInput,
                onValueChange = { onEvent(GenerateDialogViewModelCommand.OnK2UntilChange(it)) },
                label = stringResource(Res.string.k2_until),
                error = if (state.k2UntilError) stringResource(Res.string.error_until) else null
            )
        }

        FuzzyTextField(
            value = state.seedInput,
            onValueChange = { onEvent(GenerateDialogViewModelCommand.OnSeedChange(it)) },
            label = stringResource(Res.string.seed),
            error = if (state.seedError) stringResource(Res.string.error_seed) else null
        )

        TintedBlock(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                RadioOption(
                    text = stringResource(Res.string.append_to_set),
                    selected = !state.replace,
                    onClick = { onEvent(GenerateDialogViewModelCommand.OnReplaceChange(false)) }
                )
                RadioOption(
                    text = stringResource(Res.string.replace_set),
                    selected = state.replace,
                    onClick = { onEvent(GenerateDialogViewModelCommand.OnReplaceChange(true)) }
                )
            }
        }

        if (state.generationFailed) {
            Text(
                text = stringResource(Res.string.unknown_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
private fun GenerateDialogPreview() {
    FuzzyLabTheme {
        GenerateDialog(state = GenerateDialogViewModelState())
    }
}
