package com.example.ebaton

import io.ktor.client.request.get

class KtorService {

    private val client = KtorClient.getInstance()

    suspend fun getRandomDuck(): DuckResponse {
        return client.get("https://random-d.uk/api/v2/random")
    }
}