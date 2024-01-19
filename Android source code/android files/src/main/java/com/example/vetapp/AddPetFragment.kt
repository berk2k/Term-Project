package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.R
import com.example.vetapp.databinding.FragmentAddPetBinding
import com.example.vetapp.databinding.FragmentLoginBinding
import com.example.vetapp.models.Pet
import com.example.vetapp.models.User
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
import com.google.gson.Gson

class AddPetFragment : Fragment() {
    private var _binding: FragmentAddPetBinding? = null
    private val binding get() = _binding!!

    private lateinit var button:Button
    private lateinit var apiService: ApiService
    private lateinit var petName : EditText
    private lateinit var petSpecies : EditText
    private lateinit var petAge : EditText
    private lateinit var petBreed : EditText
    private lateinit var petColor : EditText
    private lateinit var petGender : EditText
    private lateinit var petWeight : EditText
    private lateinit  var petAllergies : EditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPetBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application

        val navView = binding.navView
        val toolbar = binding.addPetToolbar

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
/*
        val petTypes = resources.getStringArray(R.array.pet_types)
        val petGender = resources.getStringArray(R.array.pet_gender)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petGender)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
*/

        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }

        petName = binding.editTextPetName
        petSpecies = binding.editTextSpecies
        petAge = binding.editTextAge
        petBreed = binding.editTextBreed
        petColor = binding.editTextColor
        petGender = binding.editTextGender
        petWeight = binding.editTextWeight
        petAllergies = binding.editTextAllergies


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

        val addPetButton: Button = binding.addPetBtn
        addPetButton.setOnClickListener {
            addPet()
        }

        return view
    }

    private fun addPet() {

        var petName = binding.editTextPetName.text.toString()
        var petSpecies = binding.editTextSpecies.text.toString()
        var petAge = binding.editTextAge.text.toString().toInt()
        var petBreed = binding.editTextBreed.text.toString()
        var color = binding.editTextColor.text.toString()
        var gender = binding.editTextGender.text.toString()
        var weight = binding.editTextWeight.text.toString().toDouble()
        var allergies = binding.editTextAllergies.text.toString()

        // Check and handle petName
        if (TextUtils.isEmpty(petName)) {
            Toast.makeText(context, "Enter petName", Toast.LENGTH_SHORT).show()
            return
        }

        // Check and handle petAge


        // Check and handle petBreed
        if (TextUtils.isEmpty(petBreed)) {
            Toast.makeText(context, "Enter petBreed", Toast.LENGTH_SHORT).show()
            return
        }

        // Check and handle color
        if (TextUtils.isEmpty(color)) {
            Toast.makeText(context, "Enter color", Toast.LENGTH_SHORT).show()
            return
        }

        // Check and handle gender
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(context, "Enter gender", Toast.LENGTH_SHORT).show()
            return
        }

        // Check and handle weight


        // Check and handle allergies
        if (TextUtils.isEmpty(allergies)) {
            Toast.makeText(context, "Enter allergies", Toast.LENGTH_SHORT).show()
            return
        }

        //

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
        Log.d("userID from addPet page", retrievedUserId.toString())
        // Log the values
        Log.d("PetDetails", "Pet Name: $petName")
        Log.d("PetDetails", "Pet Species: $petSpecies")
        Log.d("PetDetails", "Pet Age: $petAge years")
        Log.d("PetDetails", "Pet Breed: $petBreed")
        Log.d("PetDetails", "Color: $color")
        Log.d("PetDetails", "Gender: $gender")
        Log.d("PetDetails", "Weight: $weight kg")
        Log.d("PetDetails", "Allergies: $allergies")
        val newPet = Pet(id = retrievedUserId ,name = petName, species = petSpecies , breed = petBreed , color = color , age = petAge , gender = gender , weight= weight, allergies = allergies )

        val gson = Gson()
        val requestBody = gson.toJson(newPet)
        Log.d("API Request", "Request Body: $requestBody")

        val call = apiService.AddPet(newPet)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
                if (response.isSuccessful) {
                    // Kayıt başarılı
                    Log.d("addPet", "Pet added successfully")
                    Toast.makeText(context, "Kayıt başarıyla tamamlandı.", Toast.LENGTH_SHORT).show()
                } else {
                    // Kayıt başarısız, hata mesajını kontrol et
                    val errorBody = response.errorBody()?.string()
                    print(errorBody)

                    Log.d("addPet", "Pet cannot be added")
                    if (errorBody != null) {
                        Log.d("addPet", errorBody)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.userFragment -> {
                val action =
                    AddPetFragmentDirections.actionAddPetFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    AddPetFragmentDirections.actionAddPetFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    AddPetFragmentDirections.actionAddPetFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    AddPetFragmentDirections.actionAddPetFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    AddPetFragmentDirections.actionAddPetFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }


}