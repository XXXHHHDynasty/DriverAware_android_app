package com.example.driveaware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driveaware.databinding.FragmentRecordListBinding

private const val TAG = "RecordListFragment"

class RecordListFragment : Fragment() {

    private var _binding: FragmentRecordListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val recordListViewModel: RecordListViewModel by viewModels()
    private val cameraModel: CameraModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${cameraModel.getAllData().size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordListBinding.inflate(inflater, container, false)

        binding.recordRecyclerView.layoutManager = LinearLayoutManager(context)

        val records = cameraModel.getAllData();

        val adapter = RecordListAdapter(records,
            { findNavController().navigate(R.id.action_recordListFragment_to_unsafeDetailFragment) })
        binding.recordRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}