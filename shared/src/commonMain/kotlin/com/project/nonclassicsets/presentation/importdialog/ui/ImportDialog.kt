package com.project.nonclassicsets.presentation.importdialog.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.model.ImportPreview
import com.project.nonclassicsets.domain.model.InvalidRow
import com.project.nonclassicsets.domain.model.InvalidRowReason
import com.project.nonclassicsets.presentation.importdialog.state.ImportDialogViewModelState
import com.project.nonclassicsets.presentation.importdialog.viewmodel.ImportDialogEvent
import com.project.nonclassicsets.presentation.importdialog.viewmodel.ImportDialogViewModel
import com.project.nonclassicsets.presentation.importdialog.viewmodel.ImportDialogViewModelCommand
import com.project.nonclassicsets.presentation.importdialog.viewmodel.ImportDialogViewModelError
import com.project.nonclassicsets.presentation.shared.FuzzyDialog
import com.project.nonclassicsets.presentation.shared.PrimaryButton
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.shared.TintedBlock
import com.project.nonclassicsets.presentation.shared.formatDecimal
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.cancel
import nonclassicsets.shared.generated.resources.column_k1
import nonclassicsets.shared.generated.resources.column_k2
import nonclassicsets.shared.generated.resources.import_change
import nonclassicsets.shared.generated.resources.import_choose
import nonclassicsets.shared.generated.resources.import_confirm
import nonclassicsets.shared.generated.resources.import_invalid_line
import nonclassicsets.shared.generated.resources.import_more_rows
import nonclassicsets.shared.generated.resources.import_no_file
import nonclassicsets.shared.generated.resources.import_read_error
import nonclassicsets.shared.generated.resources.import_some_invalid
import nonclassicsets.shared.generated.resources.import_title
import nonclassicsets.shared.generated.resources.import_empty_file
import nonclassicsets.shared.generated.resources.quantities
import nonclassicsets.shared.generated.resources.reason_format
import nonclassicsets.shared.generated.resources.reason_k1
import nonclassicsets.shared.generated.resources.reason_k2
import nonclassicsets.shared.generated.resources.reason_number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val PREVIEW_ROWS = 5
private const val LISTED_INVALID_ROWS = 4

@Composable
fun ImportDialogRoot(
    onDismiss: () -> Unit = {},
    viewModel: ImportDialogViewModel = metroViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val onEvent = viewModel::onEvent

    LaunchedEffect(Unit) {
        onEvent(ImportDialogViewModelCommand.ChooseFile)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ImportDialogEvent.Dismiss -> onDismiss()
            }
        }
    }

    ImportDialog(state = state, onEvent = onEvent, onDismiss = onDismiss)
}

@Composable
fun ImportDialog(
    state: ImportDialogViewModelState,
    onEvent: (ImportDialogViewModelCommand) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val preview = state.preview
    val validCount = preview?.valid?.size ?: 0

    FuzzyDialog(
        width = 560.dp,
        title = stringResource(Res.string.import_title),
        actions = {
            SecondaryButton(text = stringResource(Res.string.cancel), onClick = onDismiss)
            PrimaryButton(
                text = stringResource(
                    Res.string.import_confirm,
                    pluralStringResource(Res.plurals.quantities, validCount, validCount)
                ),
                enabled = validCount > 0 && !state.isLoading,
                onClick = { onEvent(ImportDialogViewModelCommand.Import) }
            )
        }
    ) {
        TintedBlock(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = preview?.fileName ?: stringResource(Res.string.import_no_file),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(16.dp))
                SecondaryButton(
                    text = stringResource(if (preview != null) Res.string.import_change else Res.string.import_choose),
                    enabled = !state.isLoading,
                    onClick = { onEvent(ImportDialogViewModelCommand.ChooseFile) }
                )
            }
        }

        when (state.error) {
            is ImportDialogViewModelError.EmptyFile -> ErrorBlock(stringResource(Res.string.import_empty_file))
            is ImportDialogViewModelError.ReadFailed -> ErrorBlock(stringResource(Res.string.import_read_error))
            is ImportDialogViewModelError.NoError -> Unit
        }

        if (preview != null) {
            ValidationBlock(preview = preview)
            if (preview.valid.isNotEmpty()) {
                PreviewTable(rows = preview.valid)
            }
        }
    }
}

@Composable
private fun ValidationBlock(preview: ImportPreview) {
    if (preview.invalid.isEmpty()) return

    TintedBlock(
        modifier = Modifier.fillMaxWidth(),
        container = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = stringResource(
                Res.string.import_some_invalid,
                preview.invalid.size,
                pluralStringResource(Res.plurals.quantities, preview.valid.size, preview.valid.size)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        preview.invalid.take(LISTED_INVALID_ROWS).forEach { row ->
            Text(
                text = stringResource(Res.string.import_invalid_line, row.line, row.reasonText()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        if (preview.invalid.size > LISTED_INVALID_ROWS) {
            Text(
                text = stringResource(Res.string.import_more_rows, preview.invalid.size - LISTED_INVALID_ROWS),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun InvalidRow.reasonText(): String {
    return stringResource(
        when (reason) {
            is InvalidRowReason.WrongFormat -> Res.string.reason_format
            is InvalidRowReason.NotANumber -> Res.string.reason_number
            is InvalidRowReason.K1TooSmall -> Res.string.reason_k1
            is InvalidRowReason.K2NotPositive -> Res.string.reason_k2
        }
    )
}

@Composable
private fun PreviewTable(rows: List<FuzzyQuantityDraft>) {
    TintedBlock(modifier = Modifier.fillMaxWidth()) {
        PreviewRow(
            k1 = stringResource(Res.string.column_k1),
            k2 = stringResource(Res.string.column_k2),
            style = MaterialTheme.typography.bodySmall
        )
        rows.take(PREVIEW_ROWS).forEach { row ->
            PreviewRow(
                k1 = row.k1.formatDecimal(3),
                k2 = row.k2.formatDecimal(3),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (rows.size > PREVIEW_ROWS) {
            Text(
                text = stringResource(Res.string.import_more_rows, rows.size - PREVIEW_ROWS),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreviewRow(
    k1: String,
    k2: String,
    style: TextStyle,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.width(100.dp),
            text = k1,
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            modifier = Modifier.width(100.dp),
            text = k2,
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorBlock(text: String) {
    TintedBlock(
        modifier = Modifier.fillMaxWidth(),
        container = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Preview
@Composable
private fun ImportDialogPreview() {
    FuzzyLabTheme {
        ImportDialog(
            state = ImportDialogViewModelState(
                preview = ImportPreview(
                    fileName = "example.txt",
                    valid = listOf(
                        FuzzyQuantityDraft("Величина A", 6.0, 8.0),
                        FuzzyQuantityDraft("Величина B", 6.2, 8.0),
                        FuzzyQuantityDraft("Величина C", 12.0, 8.0),
                        FuzzyQuantityDraft("Величина D", 22.0, 8.0),
                    ),
                    invalid = emptyList()
                )
            )
        )
    }
}
