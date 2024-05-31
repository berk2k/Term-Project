package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.databinding.FragmentPetDetailBinding
import com.example.vetapp.models.PetResponse
import com.example.vetapp.services.ApiService
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)
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
        Log.d("userID from pet detail page", retrievedUserId.toString())

        Log.d("userID from pet detail page", retrievedUserId.toString())


        val ownerId = retrievedUserId

        val call = apiService.getPet(ownerId)

        call.enqueue(object : Callback<List<PetResponse>> {
            override fun onResponse(call: Call<List<PetResponse>>, response: Response<List<PetResponse>>) {
                if (response.isSuccessful) {
                    val pet = response.body()
                    if (pet != null) {
                        for (pet in pet){
                            binding.textViewPetName.text = pet.name.toString()
                            binding.textViewAge.text =pet.age.toString()
                            binding.textViewSpecies.text = pet.species.toString()
                            binding.textViewVaccinationDetail.text = "Vaccinated"
                        }
                    }

                } else {
                    Log.d("Error in pet detail page", "Response failed")
                }
            }

            override fun onFailure(call: Call<List<PetResponse>>, t: Throwable) {
                println("Something went wrong")
            }
        })

        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.userFragment -> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.bookAppointmentFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            R.id.vaccinationHistoryFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.notificationFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToNotificationFragment()
                findNavController().navigate(action)
            }
            R.id.chatGptFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToChatGptFragment()
                findNavController().navigate(action)
            }
            R.id.addPetFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToAddPetFragment()
                findNavController().navigate(action)
            }
            R.id.appointmentsFragment-> {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }
}