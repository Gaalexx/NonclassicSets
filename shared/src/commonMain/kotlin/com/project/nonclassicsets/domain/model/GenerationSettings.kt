package com.project.nonclassicsets.domain.model

data class GenerationSettings(
    val count: Int,
    val k1From: Double,
    val k1Until: Double,
    val k2From: Double,
    val k2Until: Double,
    val seed: Int
)
