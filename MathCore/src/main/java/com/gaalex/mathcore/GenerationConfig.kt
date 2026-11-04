package com.gaalex.mathcore

data class GenerationConfig(
    val count: Int,
    val k1Min: Double,
    val k1MaxExclusive: Double,
    val k2Min: Double,
    val k2MaxExclusive: Double,
    val seed: Int,
) {
    init {
        require(count > 0) { "count must be positive" }
        require(k1Min.isFinite() && k1MaxExclusive.isFinite()) {
            "k1 bounds must be finite"
        }
        require(k2Min.isFinite() && k2MaxExclusive.isFinite()) {
            "k2 bounds must be finite"
        }
        require(k1Min >= 2.0 && k1Min < k1MaxExclusive) {
            "k1 bounds must satisfy 2.0 <= k1Min < k1MaxExclusive"
        }
        require(k2Min > 0.0 && k2Min < k2MaxExclusive) {
            "k2 bounds must satisfy 0.0 < k2Min < k2MaxExclusive"
        }
    }
}
