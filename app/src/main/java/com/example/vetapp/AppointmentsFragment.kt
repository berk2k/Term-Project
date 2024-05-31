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
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.vetapp.databinding.FragmentDisplayAppointmentBinding
import com.example.vetapp.models.Appointment
import com.example.vetapp.models.AppointmentResponse
import com.example.vetapp.models.AppointmentsAdapter
import com.example.vetapp.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentDisplayAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayAppointmentBinding.inflate(inflater, container, false)
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

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)
        setupRetrofit()
        fetchAppointments(userId)

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

    private fun fetchAppointments(userId: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAppointment(userId).execute()
                if (response.isSuccessful) {
                    val appointments = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        setupRecyclerView(appointments)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to fetch appointments", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AppointmentsFragment", "Error fetching appointments", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error fetching appointments", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView(appointments: List<AppointmentResponse>) {
        val adapter = AppointmentsAdapter(appointments)
        binding.recyclerViewAppointments.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAppointments.adapter = adapter
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
            R.id.userFragment -> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.chatGptFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToChatGptFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }
            R.id.notificationFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToNotificationFragment()
                findNavController().navigate(action)
            }

            R.id.addPetFragment-> {
                val action =
                    AppointmentsFragmentDirections.actionAppointmentsFragmentToAddPetFragment()
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
