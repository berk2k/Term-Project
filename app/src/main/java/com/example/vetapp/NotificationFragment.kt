package com.example.vetapp

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.databinding.FragmentNotificationBinding
import com.example.vetapp.databinding.FragmentPetDetailBinding
import com.example.vetapp.models.NotificationResponse
import com.example.vetapp.models.PetResponse
import com.example.vetapp.services.ApiService
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


class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val view = binding.root
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

        binding.floatingActionButton.setOnClickListener{
            val action =
                com.example.vetapp.NotificationFragmentDirections.actionNotificationFragmentToMessageFragment()
            this.findNavController().navigate(action)
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
            Log.d("userID from notification page", retrievedUserId.toString())
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

        val call = apiService.getNotificationHistory(userId)

        call.enqueue(object : Callback<List<NotificationResponse>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<List<NotificationResponse>>, response: Response<List<NotificationResponse>>) {
                if (response.isSuccessful) {
                    val notificationList = response.body()
                    if (notificationList != null) {
                        val inflater = LayoutInflater.from(requireContext())
                        val container = view.findViewById<LinearLayout>(R.id.notificationContainer)

                        for (notification in notificationList) {
                            val cardView = inflater.inflate(R.layout.notification_card_item, container, false) as CardView

                            // Bind views
                            val dateView = cardView.findViewById<TextView>(R.id.dateView)
                            val messageView = cardView.findViewById<TextView>(R.id.messageTextView)


                            val formattedSentAt = formatSentAt(notification.sentAt)
                            // Set data
                            dateView.text = formattedSentAt // Assuming you have a date field in your notification object
                            messageView.text = notification.message

                            // Add cardView to the container
                            container.addView(cardView)
                        }
                    }
                } else {
                    Log.d("NotificationFragment", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                // Handle network or other failures
                Log.e("NotificationFragment", "Failed to fetch notification history", t)
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

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mainPageFragment -> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.userFragment -> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.appointmentsFragment -> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.messageFragment-> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToMessageFragment()
                findNavController().navigate(action)
            }
            R.id.addPetFragment -> {
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToAddPetFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }
    private fun openDrawer() {
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.openDrawer(GravityCompat.START)
    }


}