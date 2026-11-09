package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.presentation.shared.format

private val BarRowHeight = 24.dp
private val BarLabelGap = 12.dp

@Composable
fun AlphaCutBarRow(
    modifier: Modifier = Modifier,
    label: String,
    cut: AlphaCut,
    color: Color,
    scale: ChartScale,
) {
    val textMeasurer = rememberTextMeasurer()
    val cutStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val cutLabel = remember(cut) { cut.format() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BarRowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(ChartPlotStart)) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = label,
                color = color,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
        Canvas(
            modifier = Modifier
                .weight(1f)
                .padding(end = ChartPlotEnd)
                .fillMaxHeight()
        ) {
            val width = size.width
            val leftPx = ((cut.left - scale.xMin) / scale.span * width).toFloat().coerceIn(0f, width)
            val rightPx = ((cut.right - scale.xMin) / scale.span * width).toFloat().coerceIn(0f, width)
            val y = size.height / 2f
            val tick = 5.dp.toPx()
            val tickStroke = 1.5.dp.toPx()

            drawLine(color, Offset(leftPx, y), Offset(rightPx, y), strokeWidth = 3.dp.toPx())
            drawLine(color, Offset(leftPx, y - tick), Offset(leftPx, y + tick), strokeWidth = tickStroke)
            drawLine(color, Offset(rightPx, y - tick), Offset(rightPx, y + tick), strokeWidth = tickStroke)

            val layout = textMeasurer.measure(cutLabel, cutStyle)
            val gap = BarLabelGap.toPx()
            val labelWidth = layout.size.width
            val labelX = if (rightPx + gap + labelWidth <= width) {
                rightPx + gap
            } else {
                (leftPx - gap - labelWidth).coerceAtLeast(0f)
            }
            drawText(layout, topLeft = Offset(labelX, y - layout.size.height / 2f))
        }
    }
}
