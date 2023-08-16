package com.example.aqisample.data

import retrofit2.http.GET

interface AqiService {
    @GET("/api/v2/aqx_p_432?limit=1000&api_key=cebebe84-e17d-4022-a28f-81097fda5896&sort=ImportDate%20desc&format=json")
    suspend fun getAqiResult() : AqiRawResult

}