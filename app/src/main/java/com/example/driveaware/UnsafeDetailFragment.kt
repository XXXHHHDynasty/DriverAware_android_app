package com.example.driveaware

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.driveaware.databinding.FragmentUnsafeDetailBinding
import com.example.driveaware.db.RecordTable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*

class UnsafeDetailFragment : Fragment() {
    private lateinit var record: RecordTable

    private  var _binding: FragmentUnsafeDetailBinding? = null
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
        _binding =
            FragmentUnsafeDetailBinding.inflate(inflater, container, false)
        //获取点击item具体数据，包括时间，图片，经纬度
        record = Utils.record as RecordTable;
        if (!TextUtils.isEmpty(record.path)){
            val bitmap:Bitmap = BitmapFactory.decodeFile(record.path)
            binding.imageView.setImageBitmap(bitmap);
            binding.imageView.rotation = 90f;
        }
        binding.textView2.text = "Reason: " + record.reason;
        binding.textView3.text = "Time: "+getTime(record.date);
        binding.textView4.text = "Location:"+"\n"+record.longitude + ","+record.latitude;
        if(record.latitude != null && record.longitude != null) {
            var mapView = binding.mapView
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync { map ->
                var googleMap = map
                val location = LatLng(record.latitude!!, record.longitude!!)
                googleMap.addMarker(MarkerOptions().position(location).title("Location Marker"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            }
        }


        return binding.root
    }
    private fun getTime(date: Date?): String? {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(date)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}