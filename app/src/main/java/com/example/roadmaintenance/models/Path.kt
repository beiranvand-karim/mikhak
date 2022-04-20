package com.example.roadmaintenance.models;

public class Path(
     val  pathId: Long,
     val  firstPoint: String,
     val  secondPoint: String,
     val  width: Double,
     val  distanceEachLightPost: Double,
     val  cablePass: String,
     val  lightPostOnPathSides: String,
     val  lightPosts: List<LightPost>)

