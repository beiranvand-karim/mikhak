package com.example.roadmaintenance.models

class Road(
    var location:String,
    var width: Int,
    var distanceEachLightPost: Int,
    var lightPost : LightPost
) {
}