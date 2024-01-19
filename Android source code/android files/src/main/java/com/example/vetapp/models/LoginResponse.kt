package com.example.vetapp.models

data class LoginResponse(
    val statusCode: Int,
    val status: String,
    val isSuccess: Boolean,
    val errorMessage: String?,
    val result: Result
)
