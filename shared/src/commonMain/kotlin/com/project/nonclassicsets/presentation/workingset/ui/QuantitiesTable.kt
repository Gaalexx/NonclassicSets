package com.project.nonclassicsets.presentation.workingset.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.presentation.shared.CountBadge
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.format
import com.project.nonclassicsets.presentation.shared.formatLevel
import com.project.nonclassicsets.presentation.shared.formatParameter
import com.project.nonclassicsets.presentation.workingset.state.WorkingSetViewModelState
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModelCommand
import com.project.nonclassicsets.presentation.shared.displayName
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.column_cut
import nonclassicsets.shared.generated.resources.column_cut_unknown
import nonclassicsets.shared.generated.resources.column_id
import nonclassicsets.shared.generated.resources.column_k1
import nonclassicsets.shared.generated.resources.column_k2
import nonclassicsets.shared.generated.resources.column_name
import nonclassicsets.shared.generated.resources.current_set
import nonclassicsets.shared.generated.resources.cut_not_computed
import nonclassicsets.shared.generated.resources.quantities
import nonclassicsets.shared.generated.resources.row_delete
import nonclassicsets.shared.generated.resources.row_menu
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val IdColumn = 82.dp
private val NumberColumn = 132.dp
private val CutColumn = 242.dp
private val MenuColumn = 32.dp

@Composable
fun QuantitiesTableCard(
    modifier: Modifier = Modifier,
    state: WorkingSetViewModelState,
    onEvent: (WorkingSetViewModelCommand) -> Unit,
) {
    FuzzyCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.current_set),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CountBadge(
                text = pluralStringResource(Res.plurals.quantities, state.quantities.size, state.quantities.size)
            )
        }

        TableRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.surface),
            style = MaterialTheme.typography.bodySmall,
            id = stringResource(Res.string.column_id),
            name = stringResource(Res.string.column_name),
            k1 = stringResource(Res.string.column_k1),
            k2 = stringResource(Res.string.column_k2),
            cut = state.alpha?.let { stringResource(Res.string.column_cut, it.formatLevel()) }
                ?: stringResource(Res.string.column_cut_unknown),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = state.quantities, key = { "quantity-${it.id}" }) { quantity ->
                QuantityRow(
                    quantity = quantity,
                    row = state.cuts[quantity.id],
                    selected = quantity.id == state.selectedId,
                    onClick = { onEvent(WorkingSetViewModelCommand.SelectQuantity(quantity.id)) },
                    onDelete = { onEvent(WorkingSetViewModelCommand.DeleteQuantity(quantity.id)) }
                )
            }
        }
    }
}

@Composable
private fun QuantityRow(
    quantity: FuzzyQuantity,
    row: QuantityWithCut?,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TableRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick),
        style = MaterialTheme.typography.bodyMedium,
        id = quantity.id,
        name = quantity.displayName(),
        k1 = quantity.k1.formatParameter(),
        k2 = quantity.k2.formatParameter(),
        cut = row?.cut?.format() ?: stringResource(Res.string.cut_not_computed),
    ) {
        Box {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { menuExpanded = true }
                    .padding(horizontal = 8.dp),
                text = stringResource(Res.string.row_menu),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.row_delete)) },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun TableRow(
    modifier: Modifier = Modifier,
    style: TextStyle,
    id: String,
    name: String,
    k1: String,
    k2: String,
    cut: String,
    menu: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(Modifier.width(IdColumn), id, style)
        TableCell(Modifier.weight(1f), name, style)
        TableCell(Modifier.width(NumberColumn), k1, style)
        TableCell(Modifier.width(NumberColumn), k2, style)
        TableCell(Modifier.width(CutColumn), cut, style)
        Row(
            modifier = Modifier.width(MenuColumn),
            horizontalArrangement = Arrangement.Center,
            content = menu
        )
    }
}

@Composable
private fun TableCell(
    modifier: Modifier,
    text: String,
    style: TextStyle,
) {
    Text(
        modifier = modifier.padding(end = 8.dp),
        text = text,
        style = style,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
