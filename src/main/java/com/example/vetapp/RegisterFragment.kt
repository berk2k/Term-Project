package com.example.vetapp

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetapp.databinding.FragmentLoginBinding
import com.example.vetapp.databinding.FragmentRegisterBinding
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


class RegisterFragment : Fragment() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    //   private lateinit var auth: FirebaseAuth
    private lateinit var proggressBar: ProgressBar
    private lateinit var loginNow: TextView
    private lateinit var apiService: ApiService

    private  var isRegistered: Boolean = false

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*    val currentUser = auth.currentUser
            if (currentUser != null) {
                val intent = Intent(this@Register, MainActivity::class.java)
                startActivity(intent)
                finish()
            }*/
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//    auth = Firebase.auth
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        editTextEmail = binding.email
        editTextPassword = binding.password
        buttonReg = binding.btnRegister
        proggressBar = binding.prggressBar
        loginNow = binding.loginNow

        loginNow.setOnClickListener {
            //navigation
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

        val registerButton: Button = binding.btnRegister
        registerButton.setOnClickListener {
            registerUser()

            if (isRegistered){
                val action =
                    RegisterFragmentDirections.actionRegisterFragmentToMainPageFragment()
                findNavController().navigate(action)
            }

        }


        /*    buttonReg.setOnClickListener {
                proggressBar.visibility = View.VISIBLE
                var email: String
                var password: String

                email = editTextEmail.text.toString()
                password = editTextPassword.text.toString()

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        proggressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            //val user = auth.currentUser
                            Toast.makeText(
                                this,
                                "Account created.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(this@Register, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            }*/
        return view
    }
    private fun registerUser() {
        proggressBar.visibility = View.VISIBLE
        var email: String
        var password: String

        email = editTextEmail.text.toString()
        password = editTextPassword.text.toString()

        if (TextUtils.isEmpty(email)){
            Toast.makeText(context,"Enter email",Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(context,"Enter password",Toast.LENGTH_SHORT).show()
            return
        }

        //
        val newUser = User(username = email,"Berk", password = password,"Admin")

        val call = apiService.registerUser(newUser)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
                if (response.isSuccessful) {
                    // Kayıt başarılı
                    isRegistered = true
                    Toast.makeText(context, "Kayıt başarıyla tamamlandı.", Toast.LENGTH_SHORT).show()
                } else {
                    // Kayıt başarısız, hata mesajını kontrol et
                    val errorBody = response.errorBody()?.string()
                    print(errorBody)

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


}