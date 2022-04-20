package com.example.roadmaintenance.models;

data class LightPost(
    val id : Long,
    val height : Double,
    val power : Double,
    val lightProductionType : String,
    val path : Path)
