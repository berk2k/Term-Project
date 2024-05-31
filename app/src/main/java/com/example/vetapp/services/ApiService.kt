package com.example.vetapp.services

import com.example.vetapp.models.*
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

    @GET("/api/UserProfile/GetUserInfo")
    fun getUserProfile(@Query("id") userId: Int): Call<UserProfileResponse>

    @GET("/api/GetPet/GetPetNameById")
    fun getPetNameById(@Query("id") id: Int): Call<String>

    @GET("/api/GetPet/{ownerId}")
    fun getPet(@Path("ownerId") ownerId: Int): Call<List<PetResponse>>

    @GET("/api/Appointment/GetUserAppointmentsWOPagination")
    fun getAppointment(@Query("userId") userId: Int): Call<List<AppointmentResponse>>

    @POST("/api/Notification/SendMessageFromUserToVet")
    fun sendMessage(
        @Body messageRequest: MessageRequest
    ): Call<Void>

    @GET("/api/Review/GetReviewsHistoryForUserWOPagination")
    fun getTreatmentReviews(@Query("userId") userId: Int): Call<List<TreatmentReview>>

    @GET("/api/VaccinationRecord/GetAllVaccinationHistoryForUserWOPagination")
    fun getVaccineHistory(@Query("id")id:Int): Call<List<VaccineHistory>>

    @GET("/api/Notification/GetNotificationHistoryForUserWOPagination")
    fun getNotificationHistory(

        @Query("userId") userId: Int
    ): Call<List<NotificationResponse>>

}
