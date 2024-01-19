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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.R
import com.example.vetapp.databinding.FragmentMainPageBinding


class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application

        // Handle navigation icon click (if needed)

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


        binding.floatingActionButton.setOnClickListener{
            val action =
                com.example.vetapp.MainPageFragmentDirections.actionMainPageFragmentToAddPetFragment()
            this.findNavController().navigate(action)
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val retrievedUserId = sharedPreferences.getInt("userId", 0) // defaultValue is the value to return if the key is not found
            Log.d("userID from main page", retrievedUserId.toString())
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.bottom_navigation_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.userFragment -> {
                val action =
                    MainPageFragmentDirections.actionMainPageFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.bookAppointmentFragment -> {
                val action =
                    MainPageFragmentDirections.actionMainPageFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            R.id.treatmentReviewFragment-> {
                val action =
                    MainPageFragmentDirections.actionMainPageFragmentToTreatmentReviewFragment()
                findNavController().navigate(action)
            }

            R.id.vaccinationHistoryFragment-> {
                val action =
                    MainPageFragmentDirections.actionMainPageFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    MainPageFragmentDirections.actionMainPageFragmentToPetDetailFragment()
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