package com.example.vetapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.R
import com.example.vetapp.TreatmentReviewFragmentDirections
import com.example.vetapp.databinding.FragmentTreatmentReviewBinding
import com.example.vetapp.models.NotificationResponse

import com.example.vetapp.models.PetResponse
import com.example.vetapp.models.TreatmentReview
import com.example.vetapp.services.ApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TreatmentReviewFragment : Fragment() {
    private var _binding: FragmentTreatmentReviewBinding? = null
    private val binding get() = _binding!!


    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTreatmentReviewBinding.inflate(inflater, container, false)
        val view = binding.root
        val navView = binding.navView
        val drawer = binding.drawerLayout
        val toolbar = binding.materialToolbar
        val navHostFragment = (activity as AppCompatActivity).supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView, navController)

        val builder = AppBarConfiguration.Builder(navController.graph)
        val appBarConfiguration = builder.build()
        //toolbar.setupWithNavController(navController,appBarConfiguration)

        val bottomNavView = binding.bottomNav
        bottomNavView.setupWithNavController(navController)

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


        toolbar.setNavigationOnClickListener {
            drawer.open()
        }
        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }
        val userId = retrievedUserId

        val call = apiService.getTreatmentReviews(userId)



        call.enqueue(object : Callback<List<TreatmentReview>> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<List<TreatmentReview>>, response: Response<List<TreatmentReview>>) {
                if (response.isSuccessful) {
                    val treatment = response.body()
                    if (treatment != null) {
                        val inflater = LayoutInflater.from(requireContext())
                        val container = view.findViewById<LinearLayout>(R.id.reviewsContainer)

                        for (treatment in treatment) {
                            val cardView = inflater.inflate(R.layout.item_treatment_review, container, false) as CardView

                            // Bind views
                            // val dateView = cardView.findViewById<TextView>(R.id.dateView)
                            val date = cardView.findViewById<TextView>(R.id.dateView)
                            val name = cardView.findViewById<TextView>(R.id.petView)
                            val review = cardView.findViewById<TextView>(R.id.reviewText)
                            // Set data
                            //dateView.text = notification.date // Assuming you have a date field in your notification object

                            date.text = "sent at: "+formatSentAt(treatment.sentAt)

                            val petcall = apiService.getPet(userId)
                            petcall.enqueue(object : Callback<List<PetResponse>> {
                                override fun onResponse(call: Call<List<PetResponse>>, response: Response<List<PetResponse>>) {
                                    if (response.isSuccessful) {
                                        val petName = response.body()
                                        if (petName != null) {
                                            for (petName in petName){
                                                name.text = "pet name: "+petName.name
                                            }
                                        }

                                    } else {
                                        println("petName1")
                                    }
                                }

                                override fun onFailure(call: Call<List<PetResponse>>, t: Throwable) {
                                    println("petName2")
                                }
                            })

                            review.text = "review: "+treatment.message
                            // Add cardView to the container
                            container.addView(cardView)
                        }
                    }
                } else {
                    Log.d("TreatmentFragment", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TreatmentReview>>, t: Throwable) {
                // Handle network or other failures
                Log.e("TreatmentFragment", "Failed to fetch treatment history", t)
            }
        })

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatSentAt(sentAt: String): String {
        // Define the input format with optional milliseconds
        val inputFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .optionalEnd()
            .toFormatter()
        // Parse the input string to a LocalDateTime object
        val dateTime = LocalDateTime.parse(sentAt, inputFormatter)
        // Define the desired output format
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        // Format the LocalDateTime object to the desired format
        return dateTime.format(outputFormatter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.userFragment -> {
                val action = TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action = TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment -> {
                val action = TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.bookAppointmentFragment -> {
                val action = TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            R.id.vaccinationHistoryFragment -> {
                val action = TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.notificationFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToNotificationFragment()
                findNavController().navigate(action)
            }
            R.id.chatGptFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToNotificationFragment()
                findNavController().navigate(action)
            }
            R.id.addPetFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToAddPetFragment()
                findNavController().navigate(action)
            }
            R.id.appointmentsFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
            else -> return false
        }
        return true
    }
}
