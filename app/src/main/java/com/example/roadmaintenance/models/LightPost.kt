package com.example.roadmaintenance.models

enum class LightPostSideEnum {
    ONE_SIDE,
    TWO_SIDES
}

class LightPost(
    var id: Int,
    var height: Int,
    var power: Int,
    var lightProductionType: String,
    var electricalPostSides: LightPostSideEnum
) {
}