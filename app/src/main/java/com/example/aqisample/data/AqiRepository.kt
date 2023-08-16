package com.example.aqisample.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AqiRepository @Inject constructor(
    private val service: AqiService,
    private val converter: AqiConverter
) {
    suspend fun getAqiList(): List<AqiData> {
        return service.getAqiResult().records.map(converter::convert)
    }
}