package com.example.driveaware

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    //1、首先声明一个数组permissions，将需要的权限都放在里面
    lateinit var permissions:Array<String> ;
    val mPermissionList = ArrayList<String>()
    val mRequestCode = 0x1//权限请求码

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2){
            permissions =  arrayOf<String>(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }else{
            permissions =  arrayOf<String>(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        initPermission()
    }

    fun initPermission() {
        mPermissionList.clear()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionList.add(permission)
            }
        }
        if (!mPermissionList.isEmpty()) {
            // 后续操作...
            ActivityCompat.requestPermissions(this@MainActivity, permissions, mRequestCode)

        } else {
            Toast.makeText(this@MainActivity, "全部授予！", Toast.LENGTH_SHORT).show()


        }
    }

    //重写
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0x1 -> for (i in 0 until grantResults.size) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) Toast.makeText(
                    this,
                    "您有未授予的权限，可能影响使用",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }// 授权结束后的后续操作
    }
}