package com.example.roadmaintenance.models;

public class Path(
    val columnId: Long,
    val pathId: Double,
    val firstPoint: String,
    val secondPoint: String,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: String,
    val lightPosts: List<LightPost>
)

