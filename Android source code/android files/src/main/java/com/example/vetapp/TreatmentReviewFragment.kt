package com.example.vetapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetapp.databinding.FragmentTreatmentReviewBinding
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text


class TreatmentReviewFragment : Fragment() {
    private var _binding: FragmentTreatmentReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var date : TextView
    private lateinit var name : TextView
    private lateinit var review : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTreatmentReviewBinding.inflate(inflater,container,false)
        val view = binding.root



        date = binding.dateView
        name = binding.nameView
        review = binding.reviewText

        date.text = "17.01.2024"
        name.text = "Cem"
        review.text = "Nothing to worry about"

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
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToUserFragment()
                findNavController().navigate(action)
            }
            R.id.mainPageFragment -> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
            R.id.petDetailFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToPetDetailFragment()
                findNavController().navigate(action)
            }

            R.id.bookAppointmentFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToBookAppointmentFragment()
                findNavController().navigate(action)
            }
            R.id.vaccinationHistoryFragment-> {
                val action =
                    TreatmentReviewFragmentDirections.actionTreatmentReviewFragmentToVaccinationHistoryFragment()
                findNavController().navigate(action)
            }
            else -> {
                return false
            }

        }

        return true
    }
}