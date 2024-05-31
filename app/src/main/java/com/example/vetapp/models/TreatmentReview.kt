package com.example.vetapp.models
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vetapp.R



data class TreatmentReview(
    val userId: Int,
    val petId: Int,
    val message: String,
    val sentAt: String

)





