package com.project.nonclassicsets.data.repository

import com.gaalex.mathcore.clusterization.AgglomerativeClusterer
import com.gaalex.mathcore.clusterization.ClusterMode
import com.gaalex.mathcore.clusterization.EndpointIntervalDistance
import com.gaalex.mathcore.clusterization.IntervalClusterer
import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.gaalex.mathcore.fuzzy.FisherFuzzyValue
import com.gaalex.mathcore.fuzzy.FisherParameters
import com.gaalex.mathcore.fuzzy.FuzzyValue
import com.gaalex.mathcore.fuzzy.operations.FuzzyArithmetic
import com.gaalex.mathcore.generation.FisherDatasetGenerator
import com.gaalex.mathcore.generation.FisherFuzzyDatasetGenerator
import com.gaalex.mathcore.generation.GenerationConfig
import com.gaalex.mathcore.interval.Interval
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.domain.model.Cluster
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.model.GenerationSettings
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.domain.model.StopMode
import com.project.nonclassicsets.domain.repository.FuzzyMathRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@Inject
@ContributesBinding(AppScope::class)
class FuzzyMathRepositoryImpl : FuzzyMathRepository {
    private val clusterer: IntervalClusterer = AgglomerativeClusterer(EndpointIntervalDistance)
    private val generator: FisherDatasetGenerator = FisherFuzzyDatasetGenerator()

    override suspend fun alphaCuts(
        quantities: List<FuzzyQuantity>,
        alpha: Double
    ): Result<List<QuantityWithCut>> {
        try {
            return Result.success(coroutineScope {
                quantities.map { quantity ->
                    async(Dispatchers.Default) {
                        val parameters = FisherParameters(k1 = quantity.k1, k2 = quantity.k2)
                        val cut = FisherFuzzyValue(parameters).alphaCut(alpha)
                        QuantityWithCut(
                            quantity = quantity,
                            mode = parameters.mode,
                            cut = AlphaCut(left = cut.left, right = cut.right)
                        )
                    }
                }.awaitAll()
            })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun membershipCurve(
        quantity: FuzzyQuantity,
        xMin: Double,
        xMax: Double,
        pointsCount: Int
    ): List<Double> = withContext(Dispatchers.Default) {
        val value = FisherFuzzyValue(FisherParameters(k1 = quantity.k1, k2 = quantity.k2))
        List(pointsCount) { index ->
            val x = xMin + (xMax - xMin) * index / (pointsCount - 1)
            try {
                value.membership(x)
            } catch (e: ArithmeticException) {
                0.0
            }
        }
    }

    override suspend fun cluster(
        rows: List<QuantityWithCut>,
        alpha: Double,
        stopMode: StopMode
    ): Result<ClusteringOutcome> = withContext(Dispatchers.Default) {
        try {
            val mode = when (stopMode) {
                is StopMode.ByThreshold -> ClusterMode.ByThreshold(stopMode.epsilon)
                is StopMode.ByCount -> ClusterMode.ByCount(stopMode.count)
            }
            val result = clusterer.cluster(rows.map { it.cut.toInterval() }, mode)

            Result.success(
                ClusteringOutcome(
                    alpha = alpha,
                    stopMode = stopMode,
                    clusters = result.clusters
                        .sortedBy { it.min() }
                        .mapIndexed { index, members ->
                            Cluster(
                                number = index + 1,
                                members = members.sorted().map { rows[it] }
                            )
                        }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generate(settings: GenerationSettings): Result<List<FuzzyQuantityDraft>> =
        withContext(Dispatchers.Default) {
            try {
                val config = GenerationConfig(
                    count = settings.count,
                    k1Min = settings.k1From,
                    k1MaxExclusive = settings.k1Until,
                    k2Min = settings.k2From,
                    k2MaxExclusive = settings.k2Until,
                    seed = settings.seed
                )
                Result.success(generator.generate(config).map {
                    FuzzyQuantityDraft(name = null, k1 = it.k1, k2 = it.k2)
                })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun tryMathFunction(
        first: FuzzyQuantity,
        second: FuzzyQuantity,
        func: suspend (FuzzyValue, FuzzyValue) -> AlphaLevelFuzzyValue
    ): Result<AlphaLevelFuzzyValue> {
        return try {
            val firstValue = FisherFuzzyValue(FisherParameters(first.k1, first.k2))
            val secondValue = FisherFuzzyValue(FisherParameters(second.k1, second.k2))
            Result.success(func(firstValue, secondValue))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun add(
        first: FuzzyQuantity,
        second: FuzzyQuantity
    ): Result<AlphaLevelFuzzyValue> {
        return tryMathFunction(first, second, FuzzyArithmetic()::add)
    }

    override suspend fun subtract(
        first: FuzzyQuantity,
        second: FuzzyQuantity
    ): Result<AlphaLevelFuzzyValue> {
        return tryMathFunction(first, second, FuzzyArithmetic()::subtract)
    }

    override suspend fun multiply(
        first: FuzzyQuantity,
        second: FuzzyQuantity
    ): Result<AlphaLevelFuzzyValue> {
        return tryMathFunction(first, second, FuzzyArithmetic()::multiply)
    }

    override suspend fun divide(
        first: FuzzyQuantity,
        second: FuzzyQuantity
    ): Result<AlphaLevelFuzzyValue> {
        return tryMathFunction(first, second, FuzzyArithmetic()::divide)
    }

    private fun AlphaCut.toInterval(): Interval = Interval(left, right)
}
