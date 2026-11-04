package com.gaalex.mathcore.generation

import com.gaalex.mathcore.fuzzy.FisherParameters

interface FisherDatasetGenerator {
    fun generate(config: GenerationConfig): List<FisherParameters>
}
