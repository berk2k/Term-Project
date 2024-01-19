package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vetapp.databinding.FragmentLoginBinding
import com.example.vetapp.models.LoginRequest
import com.example.vetapp.models.User
import com.example.vetapp.services.ApiService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    lateinit var buttonLogin: Button
    private lateinit var buttonPass: Button
    //    private lateinit var auth: FirebaseAuth
    private lateinit var proggressBar: ProgressBar
    private lateinit var registerNow: TextView
    private lateinit var apiService: ApiService
    var LoginStatus = false

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*  val currentUser = auth.currentUser
          if (currentUser != null) {
              val intent = Intent(this@Login, MainActivity::class.java)
              startActivity(intent)
              finish()
          }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        editTextEmail = binding.email
        editTextPassword = binding.password
        buttonLogin = binding.btnLogin
        proggressBar = binding.prggressBar
        registerNow = binding.registerNow


        registerNow.setOnClickListener {

            val action =
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
            try {
               // val intent = Intent(this@Login, Register::class.java)
               // startActivity(intent)
                //finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
/*
        buttonLogin.setOnClickListener {
            proggressBar.visibility = View.VISIBLE
            var email: String
            var password: String

            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(context,"Enter email", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(context,"Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /*    auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        proggressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext,"Login successful",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }*/
        }
        */
        buttonPass = binding.btnPass
        buttonPass.setOnClickListener {
            val action =
                com.example.vetapp.LoginFragmentDirections.actionLoginFragmentToMainPageFragment()
            this.findNavController().navigate(action)
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


        buttonLogin.setOnClickListener {
            proggressBar.visibility = View.VISIBLE
            var email: String
            var password: String

            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                // Call performLogin directly within the coroutine
                LoginStatus = performLogin(email, password)

                Log.d("loginstatus", LoginStatus.toString())

                if (LoginStatus) {
                    Log.d("loginstatus in if", LoginStatus.toString())
                    val action = LoginFragmentDirections.actionLoginFragmentToMainPageFragment()
                    findNavController().navigate(action)
                }
            }
        }
        return view
    }

    private suspend fun performLogin(username: String, password: String): Boolean {
        return try {
            val loginRequest = LoginRequest(username, password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                // Process the successful response
                val userId = loginResponse?.result?.userId
                // Do something with the userId
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                userId?.let { editor.putInt("userId", it) } // Assuming userId is the variable holding the user id value
                editor.apply()

                Log.d("performLogin", userId.toString())
                val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
                Log.d("performLogin", retrievedUserId.toString())
                Toast.makeText(context, retrievedUserId.toString(), Toast.LENGTH_SHORT).show()

                true
            } else {
                // Handle unsuccessful response
                val errorBody = response.errorBody()?.string()
                Log.d("Login unsuccessful", "unsuccessful: $errorBody")
                false
            }
        } catch (e: Exception) {
            // Handle network or other exceptions
            Log.e("performLogin", "Exception: ${e.message}", e)
            false
        }
    }

}