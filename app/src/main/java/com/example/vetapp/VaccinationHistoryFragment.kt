package com.example.vetapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.vetapp.databinding.FragmentTreatmentReviewBinding
import com.example.vetapp.databinding.FragmentVaccinationHistoryBinding
import com.example.vetapp.models.PetResponse
import com.example.vetapp.models.TreatmentReview
import com.example.vetapp.models.VaccineHistory
import com.example.vetapp.services.ApiService
import okhttp3.OkHttpClient
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class VaccinationHistoryFragment : Fragment() {
    private var _binding: FragmentVaccinationHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var date : TextView
    private lateinit var checkBox: CheckBox
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVaccinationHistoryBinding.inflate(inflater,container,false)
        val view = binding.root

        val navView = binding.navView
        val toolbar = binding.materialToolbar





        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setIcon(R.drawable.drawer)

        val drawer = binding.drawerLayout
        val navHostFragment = (activity as AppCompatActivity).supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView, navController)

        val builder = AppBarConfiguration.Builder(navController.graph)
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


        apiService = retrofit.create(ApiService::class.java)


        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
        Log.d("userID from notification page", retrievedUserId.toString())

        Log.d("userID from notification page", retrievedUserId.toString())


        val userId = retrievedUserId

        val call = apiService.getVaccineHistory(userId)



        call.enqueue(object : Callback<List<VaccineHistory>> {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<VaccineHistory>>, response: Response<List<VaccineHistory>>) {
                if (response.isSuccessful) {
                    val history = response.body()
                    if (history != null) {
                        val inflater = LayoutInflater.from(requireContext())
                        val container = view.findViewById<LinearLayout>(R.id.vaccineContainer)

                        for (history in history) {
                            val cardView = inflater.inflate(R.layout.vaccination_card_item, container, false) as CardView

                            // Bind views
                            // val dateView = cardView.findViewById<TextView>(R.id.dateView)
                            val date = cardView.findViewById<TextView>(R.id.dateView)
                            val vaccine = cardView.findViewById<TextView>(R.id.vaccineName)
                            val pet = cardView.findViewById<TextView>(R.id.petView)

                            pet.text = "pet name: "+history.petName
                            date.text = "date: "+formatSentAt(history.date)
                            vaccine.text = "vaccine name: "+history.vaccineName

                            // Add cardView to the container
                            container.addView(cardView)
                        }
                    }
                } else {
                    Log.d("TreatmentFragment", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<VaccineHistory>>, t: Throwable) {
                // Handle network or other failures
                Log.e("TreatmentFragment", "Failed to fetch treatment history", t)
            }
        })
        toolbar.setNavigationOnClickListener {
            drawer.open()
        }
        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }


        binding.bottomNav.setOnItemSelectedListener { item ->
            onOptionsItemSelected(item)

        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatSentAt(sentAt: String): String {
        // Define the input format with optional milliseconds and timezone
        val inputFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .optionalEnd()
            .appendPattern("X") // 'X' for the timezone offset
            .toFormatter()

        // Parse the input string to a ZonedDateTime object
        val dateTime = ZonedDateTime.parse(sentAt, inputFormatter)

        // Define the desired output format
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        // Format the ZonedDateTime object to the desired format
        return dateTime.format(outputFormatter)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.userFragment -> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.bookAppointmentFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.notificationFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToNotificationFragment()
                findNavController().navigate(action)
            }
            R.id.chatGptFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToChatGptFragment()
                findNavController().navigate(action)
            }
            R.id.addPetFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToAddPetFragment()
                findNavController().navigate(action)
            }
            R.id.appointmentsFragment-> {
                val action =
                    VaccinationHistoryFragmentDirections.actionVaccinationHistoryFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }
}