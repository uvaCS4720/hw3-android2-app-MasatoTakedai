package edu.nd.pmcburne.hwapp.one.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

val json = Json {
    ignoreUnknownKeys = true // Prevents crashes if the API adds new fields
    coerceInputValues = true // Uses default values if fields are missing/null
}

object RetrofitInstance {
    private const val BASE_URL = "https://ncaa-api.henrygd.me/"

    val api: NcaaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NcaaApiService::class.java)
    }
}

interface NcaaApiService {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): ScoreboardResponse
}