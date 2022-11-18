package com.example.roadmaintenance.retrofitServices.serviceFactory

class RoadShapeServiceFactory : ServiceDetailsFactory() {

    override fun getUrl(): String = "http://www.mapquestapi.com/directions/v2/"
}