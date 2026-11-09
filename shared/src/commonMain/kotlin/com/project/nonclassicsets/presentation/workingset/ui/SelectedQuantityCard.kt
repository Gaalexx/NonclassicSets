package com.project.nonclassicsets.presentation.workingset.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.chart.ChartCurve
import com.project.nonclassicsets.presentation.shared.chart.MembershipChart
import com.project.nonclassicsets.presentation.shared.displayName
import com.project.nonclassicsets.presentation.shared.format
import com.project.nonclassicsets.presentation.shared.formatDecimal
import com.project.nonclassicsets.presentation.shared.formatLevel
import com.project.nonclassicsets.presentation.shared.formatParameter
import com.project.nonclassicsets.presentation.workingset.state.WorkingSetViewModelState
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.alpha_line
import nonclassicsets.shared.generated.resources.axis_mu
import nonclassicsets.shared.generated.resources.axis_x
import nonclassicsets.shared.generated.resources.cut_at_alpha
import nonclassicsets.shared.generated.resources.max_membership
import nonclassicsets.shared.generated.resources.max_membership_value
import nonclassicsets.shared.generated.resources.preview_parameters
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectedQuantityCard(
    modifier: Modifier = Modifier,
    state: WorkingSetViewModelState,
) {
    val selected = state.selected ?: return
    val alphaText = state.alpha?.formatLevel().orEmpty()

    FuzzyCard(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selected.quantity.displayName(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(
                    Res.string.preview_parameters,
                    selected.quantity.k1.formatParameter(),
                    selected.quantity.k2.formatParameter()
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(166.dp)
            ) {
                val scale = state.selectedScale
                if (scale != null) {
                    MembershipChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(166.dp),
                        curves = listOf(
                            ChartCurve(values = state.selectedCurve, color = MaterialTheme.colorScheme.primary)
                        ),
                        scale = scale,
                        alpha = state.alpha,
                        alphaLabel = stringResource(Res.string.alpha_line, alphaText),
                        muLabel = stringResource(Res.string.axis_mu),
                        xLabel = stringResource(Res.string.axis_x),
                    )
                }
            }

            Column(
                modifier = Modifier.width(330.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.max_membership),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.max_membership_value, selected.mode.formatDecimal(3, 1)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = stringResource(Res.string.cut_at_alpha, alphaText),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selected.cut.format(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
