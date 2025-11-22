package com.example.verstka_last.search.data.network

import com.example.verstka_last.search.data.dto.ITunesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesAPI {
    @GET("/search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<ITunesSearchResponse>
}