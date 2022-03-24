package com.example.roadmaintenance.models

data class Path(
    var id: Int,
    var location: String,
    var width: Int,
    var height: Int,
    var distanceEachLightPost: Int,
    var power: Int,
    var lightProductionType: String,
    var electricalPostSides: LightPostSideEnum
) {
}