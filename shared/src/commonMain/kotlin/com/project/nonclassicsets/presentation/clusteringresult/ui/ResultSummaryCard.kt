package com.project.nonclassicsets.presentation.clusteringresult.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.domain.model.Cluster
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.StopMode
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.formatLevel
import com.project.nonclassicsets.presentation.theme.clusterColor
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.cluster_title
import nonclassicsets.shared.generated.resources.clusters_composition
import nonclassicsets.shared.generated.resources.clusters_short
import nonclassicsets.shared.generated.resources.quantities
import nonclassicsets.shared.generated.resources.quantities_short
import nonclassicsets.shared.generated.resources.summary
import nonclassicsets.shared.generated.resources.summary_alpha
import nonclassicsets.shared.generated.resources.summary_count
import nonclassicsets.shared.generated.resources.summary_epsilon
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultSummaryCard(
    modifier: Modifier = Modifier,
    outcome: ClusteringOutcome,
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    FuzzyCard(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "title") {
                Text(
                    text = stringResource(Res.string.summary),
                    style = MaterialTheme.typography.headlineSmall,
                    color = textColor
                )
            }

            item(key = "numbers") {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryNumber(
                        modifier = Modifier.weight(1f),
                        value = outcome.quantitiesCount,
                        label = pluralStringResource(Res.plurals.quantities_short, outcome.quantitiesCount)
                    )
                    SummaryNumber(
                        modifier = Modifier.weight(1f),
                        value = outcome.clusters.size,
                        label = pluralStringResource(Res.plurals.clusters_short, outcome.clusters.size)
                    )
                }
            }

            item(key = "divider_top") {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            item(key = "alpha") {
                SummaryValue(stringResource(Res.string.summary_alpha), outcome.alpha.formatLevel())
            }

            item(key = "stop_mode") {
                when (val mode = outcome.stopMode) {
                    is StopMode.ByThreshold -> SummaryValue(stringResource(Res.string.summary_epsilon), mode.epsilon.formatLevel())
                    is StopMode.ByCount -> SummaryValue(stringResource(Res.string.summary_count), mode.count.toString())
                }
            }

            item(key = "divider_bottom") {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            item(key = "composition_title") {
                Text(
                    text = stringResource(Res.string.clusters_composition),
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }

            items(outcome.clusters, key = { "cluster_${it.number}" }) { cluster ->
                ClusterCard(cluster = cluster)
            }
        }
    }
}

@Composable
private fun SummaryNumber(
    modifier: Modifier = Modifier,
    value: Int,
    label: String,
) {
    Column(modifier = modifier) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SummaryValue(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ClusterCard(cluster: Cluster) {
    val color = clusterColor(cluster.number)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.container)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.cluster_title, cluster.number),
                style = MaterialTheme.typography.titleMedium,
                color = color.content
            )
            Text(
                text = pluralStringResource(Res.plurals.quantities, cluster.members.size, cluster.members.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = cluster.members.joinToString(", ") { it.quantity.name ?: it.quantity.id },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
