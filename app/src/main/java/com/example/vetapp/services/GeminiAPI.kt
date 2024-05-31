package com.example.vetapp.services

import com.android.volley.Response
import com.example.vetapp.models.ChatMessage
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiAPI {
    @POST("/chat")
    suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body message: ChatMessage
    ): Response<ChatMessage>
}