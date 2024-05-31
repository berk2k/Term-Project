package com.example.vetapp.models


data class MessageRequest(
    val userId: Int,
    val messageText: String,
    val messageTitle: String
)
