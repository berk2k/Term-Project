package com.example.vetapp.models

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.vetapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Appointment(
    val appointmentId: Int,
    val appointmentDateTime: String,
    val clientName: String,
    val petName: String,
    val reasons: String
)

class AppointmentsAdapter(private val appointmentList: List<AppointmentResponse>) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointmentList[position]
        holder.appointmentDateTime.text = formatSentAt(currentItem.appointmentDateTime.toString())
        holder.clientName.text = currentItem.clientName
        holder.petName.text = currentItem.petName
        holder.reasons.text = currentItem.reasons
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatSentAt(sentAt: String): String {
        // Define the input format
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        // Parse the input string to a LocalDateTime object
        val dateTime = LocalDateTime.parse(sentAt, inputFormatter)
        // Define the desired output format
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        // Format the LocalDateTime object to the desired format
        return dateTime.format(outputFormatter)
    }

    override fun getItemCount() = appointmentList.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appointmentDateTime: TextView = itemView.findViewById(R.id.tvAppointmentDateTime)
        val clientName: TextView = itemView.findViewById(R.id.tvClientName)
        val petName: TextView = itemView.findViewById(R.id.tvPetName)
        val reasons: TextView = itemView.findViewById(R.id.tvReasons)
    }
}


