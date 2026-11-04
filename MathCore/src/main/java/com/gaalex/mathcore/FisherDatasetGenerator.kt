package com.gaalex.mathcore

interface FisherDatasetGenerator {
    fun generate(config: GenerationConfig): List<FisherParameters>
}
