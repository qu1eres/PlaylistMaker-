package com.example.verstka_last.core.data.network

import com.example.verstka_last.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}