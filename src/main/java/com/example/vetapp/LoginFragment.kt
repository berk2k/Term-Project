package com.example.vetapp

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.vetapp.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputEditText


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonPass: Button
    //    private lateinit var auth: FirebaseAuth
    private lateinit var proggressBar: ProgressBar
    private lateinit var registerNow: TextView

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
        buttonPass = binding.btnPass
        buttonPass.setOnClickListener {
            val action =
                com.example.vetapp.LoginFragmentDirections.actionLoginFragmentToMainPageFragment()
            this.findNavController().navigate(action)
        }
        return view
    }
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
}