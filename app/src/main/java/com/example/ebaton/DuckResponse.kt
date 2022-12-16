package com.example.ebaton

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DuckResponse(
    @SerialName("message")
    val message: String?,

    @SerialName("url")
    val url: String?,
)
