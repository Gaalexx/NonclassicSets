package com.project.nonclassicsets.data.files

import com.project.nonclassicsets.domain.model.Cluster
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.StopMode

object LogMessages {
    fun added(quantities: List<FuzzyQuantity>): String {
        return withQuantities("Добавлено величин: ${quantities.size}", quantities)
    }

    fun replaced(quantities: List<FuzzyQuantity>): String {
        return withQuantities("Набор заменён, величин: ${quantities.size}", quantities)
    }

    fun removed(quantity: FuzzyQuantity): String {
        return "Удалена величина ${quantity.toLogLine()}"
    }

    fun clustered(outcome: ClusteringOutcome): String {
        val header = "Кластеризация: α = ${outcome.alpha}, ${outcome.stopMode.toLogText()}, " +
                "кластеров: ${outcome.clusters.size}"
        return (listOf(header) + outcome.clusters.flatMap { it.toLogLines() }).joinToString("\n")
    }

    private fun withQuantities(header: String, quantities: List<FuzzyQuantity>): String {
        return (listOf(header) + quantities.map { it.toLogLine() }).joinToString("\n")
    }

    private fun Cluster.toLogLines(): List<String> {
        return listOf("Кластер $number") + members.map { it.quantity.toLogLine() }
    }

    private fun FuzzyQuantity.toLogLine(): String {
        return "[${name ?: id}]: k1 = $k1 k2 = $k2"
    }

    private fun StopMode.toLogText(): String {
        return when (this) {
            is StopMode.ByThreshold -> "порог ε = $epsilon"
            is StopMode.ByCount -> "заданное число кластеров = $count"
        }
    }
}
