import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import com.example.vetapp.R
import com.example.vetapp.databinding.ActivityAddingNewPetBinding

class AddPet : Fragment() {

    private var _binding: ActivityAddingNewPetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ActivityAddingNewPetBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application

        val toolbar = binding.addPetToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val petTypes = resources.getStringArray(R.array.pet_types)
        val petGender = resources.getStringArray(R.array.pet_gender)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPetType.adapter = adapter

        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petGender)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPetGender.adapter = genderAdapter


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
