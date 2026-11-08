package com.project.nonclassicsets.presentation.clusteringresult.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import nonclassicsets.shared.generated.resources.cluster_members
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

private const val MAX_LISTED_MEMBERS = 8

fun Cluster.membersLabel(): String {
    val ids = members.map { it.quantity.id }
    return if (ids.size <= MAX_LISTED_MEMBERS) {
        ids.joinToString(", ")
    } else {
        ids.take(MAX_LISTED_MEMBERS).joinToString(", ") + ", …"
    }
}

@Composable
fun ResultSummaryCard(
    modifier: Modifier = Modifier,
    outcome: ClusteringOutcome,
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    FuzzyCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(Res.string.summary),
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )

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

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SummaryValue(stringResource(Res.string.summary_alpha), outcome.alpha.formatLevel())
            when (val mode = outcome.stopMode) {
                is StopMode.ByThreshold -> SummaryValue(stringResource(Res.string.summary_epsilon), mode.epsilon.formatLevel())
                is StopMode.ByCount -> SummaryValue(stringResource(Res.string.summary_count), mode.count.toString())
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = stringResource(Res.string.clusters_composition),
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )

            outcome.clusters.forEach { cluster ->
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
        Text(
            text = stringResource(Res.string.cluster_title, cluster.number),
            style = MaterialTheme.typography.titleMedium,
            color = color.content
        )
        Text(
            text = stringResource(
                Res.string.cluster_members,
                cluster.membersLabel(),
                pluralStringResource(Res.plurals.quantities, cluster.members.size, cluster.members.size)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
