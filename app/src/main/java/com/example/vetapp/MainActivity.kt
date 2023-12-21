package com.example.vetapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    private lateinit var detail: TextView
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    //    auth = Firebase.auth
        detail = findViewById(R.id.user_details)
        button = findViewById(R.id.logout)
    /*    user = auth.currentUser!!

        if (user ==null){
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }else{
            detail.text = user.email
        }*/

        button.setOnClickListener{

            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}