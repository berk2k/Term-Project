package com.example.vetapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var detail: TextView
    private lateinit var button: Button
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        detail = findViewById(R.id.user_details)
        button = findViewById(R.id.logout)
        user = auth.currentUser!!

        if (user ==null){
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }else{
            detail.text = user.email
        }

        button.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}