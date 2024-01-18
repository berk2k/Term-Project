package com.example.vetapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.vetapp.databinding.FragmentAddPetBinding
import com.example.vetapp.databinding.FragmentImmunizaionBinding
import com.google.android.material.textfield.TextInputEditText


class ImmunizaionFragment : Fragment() {
    private var _binding: FragmentImmunizaionBinding? = null
    private val binding get() = _binding!!


    private lateinit var editTextPetName : TextInputEditText
    private lateinit var editTextSpecies : TextInputEditText
    private lateinit var editTextAge : TextInputEditText
    private lateinit var editTextBreed : TextInputEditText
    private lateinit var editTextColor : TextInputEditText
    private lateinit var editTextGender : TextInputEditText
    private lateinit var editTextWeight : TextInputEditText
    private lateinit  var editTextAllergies : TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImmunizaionBinding.inflate(inflater, container, false)
        val view = binding.root

        val navView = binding.navView
        val toolbar = binding.addPetToolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setIcon(R.drawable.drawer)

        val drawer = binding.drawerLayout
        val navHostFragment = (activity as AppCompatActivity).supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView, navController)

        val builder = AppBarConfiguration.Builder(navController.graph)
        builder.setOpenableLayout(drawer)
        navView.setNavigationItemSelectedListener { item ->
            onOptionsItemSelected(item)
        }




        binding.bottomNav.setOnItemSelectedListener { item ->
            onOptionsItemSelected(item)

        }


        return view
    }

}