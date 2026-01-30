package com.example.verstka_last.core.data.network

import com.example.verstka_last.search.data.dto.Response
import com.example.verstka_last.search.data.dto.TrackRequest
import com.example.verstka_last.search.data.network.iTunesAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val iTunesService: iTunesAPI) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TrackRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesService.search(dto.expression)
                resp.apply { resultCode = 200 }
            }
            catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}