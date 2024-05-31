package com.example.vetapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController

import com.example.vetapp.databinding.FragmentMessageToVetBinding
import com.example.vetapp.models.MessageRequest

import com.example.vetapp.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageToVetBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageToVetBinding.inflate(inflater, container, false)
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
        setupRetrofit()

        val btnSend = binding.btnSend
        val etMessage = binding.etMessage
        val etMessageTitle = binding.etMessageTitle

        btnSend.setOnClickListener {
            val messageTitle = etMessageTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()
            if (messageTitle.isNotEmpty() && message.isNotEmpty()) {
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("userId", 0)
                val messageRequest = MessageRequest(userId, message , messageTitle)
                sendMessage(messageRequest)
            } else {
                Toast.makeText(context, "Please enter both title and message", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://termprojectbackend.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun sendMessage(messageRequest: MessageRequest) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.sendMessage(messageRequest).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MessageFragment", "Error sending message", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearFields() {
        binding.etMessageTitle.text.clear()
        binding.etMessage.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mainPageFragment -> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.userFragment -> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.appointmentsFragment -> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.chatGptFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToChatGptFragment()
                findNavController().navigate(action)
            }
            R.id.addPetFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToAddPetFragment()
                findNavController().navigate(action)
            }
            R.id.notificationFragment-> {
                val action =
                    MessageFragmentDirections.actionMessageFragmentToNotificationFragment()
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
