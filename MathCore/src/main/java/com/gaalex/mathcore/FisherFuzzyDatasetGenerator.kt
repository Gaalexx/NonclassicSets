package com.gaalex.mathcore

import kotlin.random.Random

class FisherFuzzyDatasetGenerator : FisherDatasetGenerator {
    override fun generate(
        config: GenerationConfig
    ): List<FisherParameters> {
        val random = Random(config.seed)

        return List(config.count) {
            FisherParameters(
                k1 = random.nextDouble(
                    config.k1Min,
                    config.k1MaxExclusive
                ),
                k2 = random.nextDouble(
                    config.k2Min,
                    config.k2MaxExclusive
                )
            )
        }
    }
}
