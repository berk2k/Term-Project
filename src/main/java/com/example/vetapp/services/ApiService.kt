package com.example.vetapp.services

import com.example.vetapp.models.Pet
import com.example.vetapp.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/Register/register") // "register" API'nin endpoint'i
    fun registerUser(@Body user: User): Call<Void>

    @POST("/api/AddPet/Add") // "Add pet" API'nin endpoint'i
    fun AddPet(@Body pet: Pet): Call<Void>
}
