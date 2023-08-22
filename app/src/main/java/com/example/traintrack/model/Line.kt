package com.example.traintrack.model

data class Line(
    val ActualPlatform: String,
    val ComputedDepartureTime: String,
    val DepartureStatus: String,
    val DirectionCode: String,
    val DirectionName: String,
    val Latitude: Double,
    val LineCode: String,
    val LineName: String,
    val Longitude: Double,
    val ScheduledDepartureTime: String,
    val ScheduledPlatform: String,
    val ServiceType: String,
    val Status: String,
    val StopCode: String,
    val TripNumber: String,
    val TripOrder: Int,
    val UpdateTime: String,

    // values based off responses
    val destination: String = DirectionName.drop(5),
    val isMoving: Boolean = !Status.equals("S")
)