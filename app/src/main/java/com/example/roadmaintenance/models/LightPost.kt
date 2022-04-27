package com.example.roadmaintenance.models;

data class LightPost(
    val columnId: Long,
    val lightPostId: Double,
    val sides: String,
    val height: Double,
    val power: Double,
    val lightProductionType: String,
    val path: Pathway
)
