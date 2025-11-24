package com.example.verstka_last.core.data.network

import com.example.verstka_last.search.data.dto.Response
import com.example.verstka_last.search.data.dto.TrackRequest
import com.example.verstka_last.search.data.network.iTunesAPI

class RetrofitNetworkClient(private val iTunesService: iTunesAPI) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackRequest) {
            val resp = iTunesService.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}