package com.example.vetapp.models

data class PetResponse(
    val ownerId: Int,
    val name: String,
    val species: String,
    val age: Int
)
