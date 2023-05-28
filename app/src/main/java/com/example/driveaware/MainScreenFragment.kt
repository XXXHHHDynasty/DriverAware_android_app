package com.example.driveaware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driveaware.databinding.FragmentMainScreenBinding
import com.example.driveaware.databinding.FragmentRecordListBinding

class MainScreenFragment : Fragment(){
    private var _binding: FragmentMainScreenBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)

        binding.apply {
            history.setOnClickListener{
                findNavController().navigate(R.id.action_mainScreenFragment_to_recordListFragment)
            }
            startRecording.setOnClickListener{
                findNavController().navigate(R.id.action_mainScreenFragment_to_cameraFragment2)
            }
        }

        // map
        binding.apply {
            map.setOnClickListener{
                findNavController().navigate(R.id.action_mainScreenFragment_to_mapsFragment)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}