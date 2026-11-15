package com.project.nonclassicsets.presentation.clusteringresult.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.domain.model.Cluster
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.domain.model.StopMode
import com.project.nonclassicsets.presentation.clusteringresult.state.ClusteringResultViewModelState
import com.project.nonclassicsets.presentation.clusteringresult.state.ResultRow
import com.project.nonclassicsets.presentation.clusteringresult.viewmodel.ClusteringResultViewModel
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.shared.chart.AlphaCutBar
import com.project.nonclassicsets.presentation.shared.chart.AlphaCutsBoundsWidth
import com.project.nonclassicsets.presentation.shared.chart.AlphaCutsChart
import com.project.nonclassicsets.presentation.shared.chart.ChartCurve
import com.project.nonclassicsets.presentation.shared.chart.ChartScale
import com.project.nonclassicsets.presentation.shared.chart.MembershipChart
import com.project.nonclassicsets.presentation.shared.formatLevel
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import com.project.nonclassicsets.presentation.theme.clusterColor
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.alpha_cuts
import nonclassicsets.shared.generated.resources.alpha_line
import nonclassicsets.shared.generated.resources.close
import nonclassicsets.shared.generated.resources.cluster_short
import nonclassicsets.shared.generated.resources.membership_functions
import nonclassicsets.shared.generated.resources.reset_zoom
import nonclassicsets.shared.generated.resources.result_title
import nonclassicsets.shared.generated.resources.zoom_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClusteringResultRoot(
    onClose: () -> Unit = {},
    viewModel: ClusteringResultViewModel = metroViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    ClusteringResult(
        state = state,
        onClose = onClose
    )
}

@Composable
fun ClusteringResult(
    state: ClusteringResultViewModelState,
    onClose: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val outcome = state.outcome
        if (outcome == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@Column
        }

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
                    text = stringResource(Res.string.result_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                SecondaryButton(text = stringResource(Res.string.close), onClick = onClose)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ChartCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    state = state,
                    outcome = outcome
                )
                ResultSummaryCard(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                    outcome = outcome
                )
            }
        }
    }
}

private val ChartHeight = 344.dp
private val ChartCardSpacing = 14.dp

@Composable
private fun ChartCard(
    modifier: Modifier = Modifier,
    state: ClusteringResultViewModelState,
    outcome: ClusteringOutcome,
) {
    val scale = state.scale
    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
    val coroutineScope = rememberCoroutineScope()

    FuzzyCard(modifier = modifier) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item(key = "title") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ChartCardSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.membership_functions),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    if (zoomState.value > zoomState.valueRange.start) {
                        Text(
                            modifier = Modifier.clickable { coroutineScope.launch { zoomState.zoom(Zoom.Content) } },
                            text = stringResource(Res.string.reset_zoom),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.zoom_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.isLoading || scale == null) {
                item(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ChartHeight)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            } else {
                item(key = "chart") {
                    MembershipChart(
                        modifier = Modifier.padding(end = AlphaCutsBoundsWidth),
                        curves = state.curves.map {
                            ChartCurve(values = it.values, color = clusterColor(it.clusterNumber).content, dashed = it.dashed)
                        },
                        scale = scale,
                        alpha = outcome.alpha,
                        alphaLabel = stringResource(Res.string.alpha_line, outcome.alpha.formatLevel()),
                        curveWidth = if (state.curves.size > 30) 1.2.dp else 2.5.dp,
                        scrollState = scrollState,
                        zoomState = zoomState
                    )
                }

                item(key = "cuts_title") {
                    Text(
                        modifier = Modifier.padding(top = ChartCardSpacing),
                        text = stringResource(Res.string.alpha_cuts),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item(key = "cuts") {
                    AlphaCutsChart(
                        bars = outcome.clusters.map { cluster ->
                            AlphaCutBar(
                                label = stringResource(Res.string.cluster_short, cluster.number),
                                cut = cluster.cut,
                                color = clusterColor(cluster.number).content
                            )
                        },
                        scale = scale,
                        scrollState = scrollState,
                        zoomState = zoomState
                    )
                }
            }
        }
    }
}

private val previewRows = listOf(
    QuantityWithCut(FuzzyQuantity("A", "Величина A", 6.0, 8.0), 0.4, AlphaCut(0.161, 0.897)),
    QuantityWithCut(FuzzyQuantity("B", "Величина B", 6.2, 8.0), 0.42, AlphaCut(0.172, 0.931)),
    QuantityWithCut(FuzzyQuantity("C", "Величина C", 12.0, 8.0), 1.0, AlphaCut(0.525, 1.905)),
    QuantityWithCut(FuzzyQuantity("D", "Величина D", 22.0, 8.0), 2.0, AlphaCut(1.165, 3.552)),
)

@Preview
@Composable
private fun ClusteringResultPreview() {
    FuzzyLabTheme {
        ClusteringResult(
            state = ClusteringResultViewModelState(
                outcome = ClusteringOutcome(
                    alpha = 0.6,
                    stopMode = StopMode.ByThreshold(0.05),
                    clusters = listOf(
                        Cluster(1, previewRows.take(2)),
                        Cluster(2, listOf(previewRows[2])),
                        Cluster(3, listOf(previewRows[3])),
                    )
                ),
                rows = listOf(
                    ResultRow(previewRows[0], 1, false),
                    ResultRow(previewRows[1], 1, true),
                    ResultRow(previewRows[2], 2, false),
                    ResultRow(previewRows[3], 3, false),
                ),
                scale = ChartScale(xMax = 6.0),
                isLoading = false
            )
        )
    }
}
