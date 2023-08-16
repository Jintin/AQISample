package com.example.aqisample.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AqiConverter @Inject constructor() {
    fun convert(data: AqiRawData) : AqiData {
        return AqiData(
            siteId = data.siteId?.toIntOrNull() ?: -1,
            siteName = data.siteName ?: "NA",
            county = data.county.orEmpty(),
            pm25 = data.pm25?.toDoubleOrNull() ?: -1.0,
            status = data.status.orEmpty()
        )
    }
}