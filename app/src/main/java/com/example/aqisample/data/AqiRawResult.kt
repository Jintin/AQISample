package com.example.aqisample.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AqiRawResult(val records: List<AqiRawData>)

@Keep
data class AqiRawData(
    @SerializedName("siteid")
    val siteId: String?,
    @SerializedName("sitename")
    val siteName: String?,
    val county: String?,
    @SerializedName("pm2.5")
    val pm25: String?,
    val status: String?
)
