package com.example.aqisample.data
data class AqiData(
    val siteId: Int,
    val siteName: String,
    val county: String,
    val pm25: Double,
    val status: String
) {
    fun isGoodWeather(): Boolean {
        return status == "良好"
    }
}