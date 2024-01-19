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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.databinding.FragmentUserBinding
import com.example.vetapp.models.UserProfileResponse
import com.example.vetapp.services.ApiService
import com.google.android.material.snackbar.Snackbar
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


class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.userImageView.setImageResource(R.drawable.user)
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

        binding.lifecycleOwner = viewLifecycleOwner

        toolbar.setNavigationOnClickListener {
            drawer.open()
        }
        binding.deleteButton.setOnClickListener {
            Snackbar.make(view,
                "Are you sure?", Snackbar.LENGTH_SHORT).setAction("Delete"){
                // Backend call delete
                val action =
                    UserFragmentDirections.actionUserFragmentToLoginFragment()
                this.findNavController().navigate(action)
            }.show()

        }
        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }

        toolbar.setNavigationOnClickListener {
            drawer.open()
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
            .baseUrl("https://10.0.2.2:7001/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        apiService = retrofit.create(ApiService::class.java)


        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
        Log.d("userID from user page", retrievedUserId.toString())
        val userId = retrievedUserId

        Log.d("userID from addPet page", retrievedUserId.toString())
        val call = apiService.getUserProfile(userId)
        call.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(
                call: Call<UserProfileResponse>,
                response: Response<UserProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    if (userProfile != null) {
                        binding.usernameTextView.text = userProfile.userName.toString()
                        binding.nameTextView.text = userProfile.name.toString()
                    }

                } else {
                    // Handle unsuccessful response
                    Log.d("Error in user page", "Response failed")
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                // Handle network or other failures
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

            R.id.mainPageFragment -> {
                val action =
                    UserFragmentDirections.actionUserFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    UserFragmentDirections.actionUserFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    UserFragmentDirections.actionUserFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    UserFragmentDirections.actionUserFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.bookAppointmentFragment-> {
                val action =
                    UserFragmentDirections.actionUserFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }
}