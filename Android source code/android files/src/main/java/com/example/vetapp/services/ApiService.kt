package com.example.vetapp.services

import com.example.vetapp.models.BookAppointmentRequest
import com.example.vetapp.models.LoginRequest
import com.example.vetapp.models.LoginResponse
import com.example.vetapp.models.Pet
import com.example.vetapp.models.PetResponse
import com.example.vetapp.models.User
import com.example.vetapp.models.UserProfileResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/Register/register") // "register" API'nin endpoint'i
    fun registerUser(@Body user: User): Call<Void>

    @POST("api/Login/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/AddPet/Add") // "Add pet" API'nin endpoint'i
    fun AddPet(@Body pet: Pet): Call<Void>

    @POST("/api/AddPet/BookAppointment")
    fun bookAppointment(@Body request: BookAppointmentRequest): Call<Void>

    @GET("/api/UserProfile")
    fun getUserProfile(@Query("id") userId: Int): Call<UserProfileResponse>

    @GET("/api/GetPet/{ownerId}")
    fun getPet(@Path("ownerId") ownerId: Int): Call<PetResponse>
}
