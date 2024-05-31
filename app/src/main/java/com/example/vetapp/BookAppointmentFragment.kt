package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.databinding.FragmentBookAppointmentBinding
import com.example.vetapp.databinding.FragmentLoginBinding
import com.example.vetapp.databinding.FragmentMainPageBinding
import com.example.vetapp.models.BookAppointmentRequest
import com.example.vetapp.models.Pet
import com.example.vetapp.services.ApiService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class BookAppointmentFragment : Fragment() {
    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService
    private lateinit var date : EditText
    private lateinit var time : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application

        val navView = binding.navView
        val toolbar = binding.materialToolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //(activity as AppCompatActivity).supportActionBar?.setIcon(R.drawable.drawer)

        val drawer = binding.drawerLayout
        val navHostFragment = (activity as AppCompatActivity).supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView, navController)

        val builder = AppBarConfiguration.Builder(navController.graph)
        val appBarConfiguration = builder.build()
        //toolbar.setupWithNavController(navController,appBarConfiguration)

        val bottomNavView = binding.bottomNav
        bottomNavView.setupWithNavController(navController)
        builder.setOpenableLayout(drawer)
        toolbar.setNavigationOnClickListener {
            drawer.open()
        }
        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            onOptionsItemSelected(item)

        }
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val trustAllManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // No-op: Accept all client certificates
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // No-op: Accept all server certificates
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }

        val trustAllSSLSocketFactory = SSLContext.getInstance("SSL").apply {
            init(null, arrayOf(trustAllManager), SecureRandom())
        }.socketFactory

        val client = OkHttpClient.Builder()
            .sslSocketFactory(trustAllSSLSocketFactory, trustAllManager)
            .hostnameVerifier { _, _ -> true }
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://termprojectbackend.azurewebsites.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        date = binding.editTextDate
        time = binding.editTextTime

        apiService = retrofit.create(ApiService::class.java)
        val bookAppointmentButton: Button = binding.bookAppointmentBtn
        bookAppointmentButton.setOnClickListener {
            bookAppointment()
        }

        return view
    }
    private fun bookAppointment() {

        var date = binding.editTextDate.text.toString()
        var time = binding.editTextTime.text.toString()
/*
// Combine date and time into a single string
        val dateTimeString = "$date $time"

// Define the input format based on your expected input format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

// Parse the input string to obtain a Date object
        val dateObject: Date = inputFormat.parse(dateTimeString) ?: Date()

// Define the output format
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

// Format the Date object to obtain the desired output
        val formattedDateTimeString: String = outputFormat.format(dateObject)

 */
        // Combine date and time into a single string
        val dateTimeString = "$date $time"
        val inputFormat = SimpleDateFormat("MM.dd.yyyy HH:mm", Locale.getDefault())

// Parse the input string to obtain a Date object
        val dateObject: Date = inputFormat.parse(dateTimeString) ?: Date()

// Define the output format
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set timezone to UTC

// Format the Date object to obtain the desired output
        val formattedDateTimeString: String = outputFormat.format(dateObject)
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
        Log.d("userID from addPet page", retrievedUserId.toString())
        // Log the values
        val bookAppointmentRequest = BookAppointmentRequest(id = retrievedUserId, appointmentDateTime = formattedDateTimeString)

        val gson = Gson()
        val requestBody = gson.toJson(bookAppointmentRequest)
        Log.d("API Request", "Request Body: $requestBody")

        val call = apiService.bookAppointment(bookAppointmentRequest)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
                if (response.isSuccessful) {
                    // Kayıt başarılı
                    Log.d("book", "Booked Appointment successful.")
                    Toast.makeText(context, "Kayıt başarıyla tamamlandı.", Toast.LENGTH_SHORT).show()
                } else {
                    // Kayıt başarısız, hata mesajını kontrol et
                    val errorBody = response.errorBody()?.string()
                    print(errorBody)

                    Log.d("addPet", "Pet cannot be added")
                    if (errorBody != null) {
                        Log.d("book", errorBody)
                    }
                    val errorMessage = "Registration failed: $errorBody"
                    // Hata mesajını kullanıcıya göster veya logla
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Ağ hatası veya istek başarısız oldu
                t.printStackTrace()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.userFragment -> {
                val action =
                    BookAppointmentFragmentDirections.actionBookAppointmentFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    BookAppointmentFragmentDirections.actionBookAppointmentFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    BookAppointmentFragmentDirections.actionBookAppointmentFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    BookAppointmentFragmentDirections.actionBookAppointmentFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    BookAppointmentFragmentDirections.actionBookAppointmentFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }

 */
}