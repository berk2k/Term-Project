package com.example.vetapp.models

data class AppointmentResponse(
    val appointmentDateTime: String,
    val clientName: String,
    val petName: String,
    val reasons: String
)
