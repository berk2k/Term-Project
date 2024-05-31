package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vetapp.databinding.FragmentLoginBinding
import com.example.vetapp.models.LoginRequest
import com.example.vetapp.services.ApiService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonPass: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var registerNow: TextView
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize UI elements
        editTextEmail = binding.email
        editTextPassword = binding.password
        buttonLogin = binding.btnLogin
        buttonPass = binding.btnPass
        progressBar = binding.progressBar
        registerNow = binding.registerNow

        // Set up navigation to the register fragment
        registerNow.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        // Set up navigation for password button
        buttonPass.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToMainPageFragment()
            findNavController().navigate(action)
        }

        // Initialize Retrofit
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://termprojectbackend.azurewebsites.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Handle login button click
        buttonLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val loginSuccess = performLogin(email, password)

            progressBar.visibility = View.GONE
            if (loginSuccess) {
                val action = LoginFragmentDirections.actionLoginFragmentToMainPageFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Login failed. User not found or incorrect credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun performLogin(username: String, password: String): Boolean {
        return try {
            val loginRequest = LoginRequest(username, password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                val userId = loginResponse?.result?.userId
                userId?.let {
                    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putInt("userId", it)
                        apply()
                    }
                    Log.d("performLogin", "User ID: $it")
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    return true
                }
                false
            } else {
                Log.d("performLogin", "Login unsuccessful: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("performLogin", "Exception: ${e.message}", e)
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
