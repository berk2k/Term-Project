package com.example.vetapp.models

data class PetResponse(
    val petId: Int,
    val name: String,
    val species: String,
    val breed: String,
    val color: String,
    val age: Int,
    val gender: String,
    val weight: Int,
    val allergies: String
)


