package com.example.verstka_last.data

import com.example.verstka_last.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}