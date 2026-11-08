package com.project.nonclassicsets.presentation.adddialog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.nonclassicsets.presentation.adddialog.state.AddQuantityDialogViewModelState
import com.project.nonclassicsets.presentation.adddialog.viewmodel.AddQuantityDialogEvent
import com.project.nonclassicsets.presentation.adddialog.viewmodel.AddQuantityDialogViewModel
import com.project.nonclassicsets.presentation.adddialog.viewmodel.AddQuantityDialogViewModelCommand
import com.project.nonclassicsets.presentation.shared.FuzzyDialog
import com.project.nonclassicsets.presentation.shared.FuzzyTextField
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.add_confirm
import nonclassicsets.shared.generated.resources.add_default_name
import nonclassicsets.shared.generated.resources.add_name
import nonclassicsets.shared.generated.resources.add_title
import nonclassicsets.shared.generated.resources.cancel
import nonclassicsets.shared.generated.resources.error_k1
import nonclassicsets.shared.generated.resources.error_k2
import nonclassicsets.shared.generated.resources.k1
import nonclassicsets.shared.generated.resources.k2
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddQuantityDialogRoot(
    onDismiss: () -> Unit = {},
    viewModel: AddQuantityDialogViewModel = metroViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val onEvent = viewModel::onEvent

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AddQuantityDialogEvent.Dismiss -> onDismiss()
            }
        }
    }

    AddQuantityDialog(state = state, onEvent = onEvent, onDismiss = onDismiss)
}

@Composable
fun AddQuantityDialog(
    state: AddQuantityDialogViewModelState,
    onEvent: (AddQuantityDialogViewModelCommand) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    FuzzyDialog(
        width = 500.dp,
        title = stringResource(Res.string.add_title),
        actions = {
            SecondaryButton(text = stringResource(Res.string.cancel), onClick = onDismiss)
            PrimaryButton(
                text = stringResource(Res.string.add_confirm),
                onClick = { onEvent(AddQuantityDialogViewModelCommand.Add) }
            )
        }
    ) {
        FuzzyTextField(
            value = state.nameInput,
            onValueChange = { onEvent(AddQuantityDialogViewModelCommand.OnNameChange(it)) },
            label = stringResource(Res.string.add_name),
            placeholder = stringResource(Res.string.add_default_name, state.nextId)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k1Input,
                onValueChange = { onEvent(AddQuantityDialogViewModelCommand.OnK1Change(it)) },
                label = stringResource(Res.string.k1),
                error = if (state.k1Error) stringResource(Res.string.error_k1) else null
            )
            FuzzyTextField(
                modifier = Modifier.weight(1f),
                value = state.k2Input,
                onValueChange = { onEvent(AddQuantityDialogViewModelCommand.OnK2Change(it)) },
                label = stringResource(Res.string.k2),
                error = if (state.k2Error) stringResource(Res.string.error_k2) else null
            )
        }
    }
}

@Preview
@Composable
private fun AddQuantityDialogPreview() {
    FuzzyLabTheme {
        AddQuantityDialog(state = AddQuantityDialogViewModelState(nextId = "E", k1Input = "10", k2Input = "8"))
    }
}
