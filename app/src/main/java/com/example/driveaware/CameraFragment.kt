package com.example.driveaware

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.*
import android.hardware.Camera
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.driveaware.databinding.FragmentCameraBinding
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import java.io.*
import java.util.*


class CameraFragment : Fragment() {
    var handler: Handler = Handler()
    lateinit var module: Module
    lateinit var applicationContext: Context
    var CPU: Device = Device.CPU
    val labelMap = mapOf<Int, String>(
        0 to "safe_driving",
        1 to "talking_phone",
        2 to "texting_phone",
        3 to "turning",
        4 to "other_activities"
    )
    lateinit var mlocation: Location;
    private val cameraModel: CameraModel by viewModels()
    private var _binding: FragmentCameraBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    var mCamera: Camera? = null
    val pictureCallback = Camera.PictureCallback { data, camera ->
        val pictureFile = File(
            Environment.getExternalStorageDirectory(),
            "aaa-" + System.currentTimeMillis() + ".jpg"
        )
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close();
//            mCamera?.stopPreview()
            mCamera?.startPreview()
            var reason = "Use Phone while driving"
            cameraModel.insert(
                mlocation!!.longitude,
                mlocation!!.latitude,
                reason,
                pictureFile.absolutePath
            );
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationContext = context.applicationContext
    }

    // Load the model from the assets folder
    private fun loadModel() {
        val assetFilePath = assetFilePath@{ assetName: String ->
            val file = File(applicationContext.cacheDir, assetName)
            if (file.exists() && file.length() > 0) {
                return@assetFilePath file.absolutePath
            }

            applicationContext.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return@assetFilePath file.absolutePath
        }
        module = LiteModuleLoader.load(assetFilePath("driver_behavior_model_v2.ptl"),  null,CPU)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
////        module = Module.load(
////            assetFilePath(
////                context,
////                "/Users/tengyibo/Desktop/final project 7/app/src/main/assets/driver_behavior_model_v1.ptl"
////            )
////        )
//        module = LiteModuleLoader.load("/Users/tengyibo/Desktop/final project 7/app/src/main/assets/driver_behavior_model_v1.ptl")
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun isCameraAvailable(): Boolean {
        val numberOfCameras = Camera.getNumberOfCameras()
        if (numberOfCameras != 0) {
            return true
        }
        return false
    }

//    fun assetFilePath(context: Context, asset: String): String {
//        val file = File(context.filesDir, asset)

//        try {
//            val inpStream: InputStream = context.assets.open(asset)
//            try {
//                val outStream = FileOutputStream(file, false)
//                val buffer = ByteArray(4 * 1024)
//                var read: Int
//
//                while (true) {
//                    read = inpStream.read(buffer)
//                    if (read == -1) {
//                        break
//                    }
//                    outStream.write(buffer, 0, read)
//                }
//                outStream.flush()
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//            return file.absolutePath
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return ""
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val file: File =
            File(Environment.getExternalStorageDirectory().absolutePath + "/driveaware")
        try {
            if (!file.exists()) {
                file.mkdirs()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val mLocationManager =
            activity!!.getSystemService(LOCATION_SERVICE) as LocationManager // 位置

        mlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!! // 网络

        // 每隔5秒  100 米的距离,更新当前位置
        mLocationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5000,
            100f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // 位置改变
                    // 在设备的位置改变时被调用
                    updateLocation(location)
                }

                //在用户禁用具有定位功能的硬件时被调用
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

                }

                // 位置服务可用
                // 在用户启动具有定位功能的硬件是被调用
                override fun onProviderEnabled(provider: String) {
                    // TODO Auto-generated method stub
                    updateLocation(mLocationManager.getLastKnownLocation(provider))
                }

                //在提供定位功能的硬件状态改变是被调用
                override fun onProviderDisabled(provider: String) {
                    // TODO Auto-generated method stub
                }

            })


        if (isCameraAvailable()) {
            mCamera = getCamera()
            setParameters(mCamera);
        }
        initViewListener();



        binding.apply {
            start.setOnClickListener {
                taskPicture()
                handler.postDelayed(runnable, 5000);

            }
            end.setOnClickListener {
                handler.removeCallbacks(runnable);
                findNavController().navigate(R.id.action_cameraFragment2_to_mainScreenFragment)
//                activity!!.finish()
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable);
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {
            take = true;
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            handler.postDelayed(this, 5000)
        }
    }

    private fun initViewListener() {
        val holder = binding.viewFinder.holder
        holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                setStartPreview(mCamera, holder);
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }


        })
    }

    private fun setParameters(camera: Camera?) {
        camera?.let {
            val params: Camera.Parameters = camera.parameters
            params.pictureFormat = ImageFormat.JPEG

            val size =
                Collections.max(params.supportedPictureSizes, object : Comparator<Camera.Size> {
                    override fun compare(lhs: Camera.Size, rhs: Camera.Size): Int {
                        return lhs.width * lhs.height - rhs.width * rhs.height
                    }
                })
            params.setPreviewSize(size.width, size.height);

            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            try {
                camera.parameters = params;
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    //遇到上面所说的情况，只能设置一个最小的预览尺寸
                    params.setPreviewSize(1920, 1080);
                    camera.parameters = params;
                } catch (e1: Exception) {
                    //到这里还有问题，就是拍照尺寸的锅了，同样只能设置一个最小的拍照尺寸
                    e1.printStackTrace();
                    try {
                        params.setPictureSize(1920, 1080);
                        camera.parameters = params;
                    } catch (ignored: Exception) {
                    }
                }

            }
        }
    }

    private fun getCamera(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return camera
    }

    fun releaseCamera() {
        mCamera?.setPreviewCallback(null)
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
    }


    private fun setStartPreview(camera: Camera?, holder: SurfaceHolder?) {
        camera?.let {
            try {
                camera.setPreviewDisplay(holder)
                camera.setDisplayOrientation(90)
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateLocation(mlocation: Location?) {
        // TODO Auto-generated method stub
        if (mlocation != null) {
            val stringBuffer = StringBuffer()
            stringBuffer.append("经度:" + mlocation.longitude)
            stringBuffer.append("纬度:" + mlocation.latitude)
            stringBuffer.append("海拔:" + mlocation.altitude)
            stringBuffer.append("速度:" + mlocation.speed)
            stringBuffer.append("方向:" + mlocation.bearing)
            Log.d("111111LoctianActivity>>", "stringBuffer:$stringBuffer")
        } else {

        }
    }

//    var module: Module = Module.load(assetFilePath(this, "model.ptl"))

    var take: Boolean = true;
    private fun taskPicture() {
        mCamera!!.setPreviewCallback { bytes, camera ->

            val size = mCamera!!.parameters.previewSize
//            loadModel()
            if (take) {
                take = false;

                try {
                    // c. 创建YUV对象
                    val image = YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null)
                    if (image != null) {

                        // d. 存为BitMap对象
                        val stream = ByteArrayOutputStream()
                        image.compressToJpeg(Rect(0, 0, size.width, size.height), 80, stream)
                        val bmp =
                            BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())

                        // model analyze
//                        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
//                            bmp,
//                            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
//                            TensorImageUtils.TORCHVISION_NORM_STD_RGB
//                        );
//                        val outputTensor: Tensor =
//                            module.forward(IValue.from(inputTensor)).toTensor();
//                        val scores = outputTensor.dataAsFloatArray
//
//                        var maxScore = -Float.MAX_VALUE
//                        var maxScoreIdx = -1
//                        for (i in 0 until scores.size) {
//                            if (scores[i] > maxScore) {
//                                maxScore = scores[i]
//                                maxScoreIdx = i
//                            }
//                        }
//                        val className: String? = labelMap.get(maxScoreIdx)

                        // e. 保存到文件 - 下面分析1
                        val pictureFile = File(
                            Environment.getExternalStorageDirectory(),
                            "aaa-" + System.currentTimeMillis().toString().replace(':','.') + ".jpg"
                        )
                        try {
                            val fos = FileOutputStream(pictureFile)
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.write(bytes)
                            fos.close();
                            mCamera?.startPreview()
                            var reason: String? ="Talking on the phone"
                            cameraModel.insert(
                                mlocation!!.longitude,
                                mlocation!!.latitude,
                                reason,
                                pictureFile.absolutePath
                            );
                            take = false;
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            var reason = "Talking on the phone"
                            cameraModel.insert(
                                mlocation!!.longitude,
                                mlocation!!.latitude,
                                reason,
                                ""
                            );
                            take = false;
                        }
                        stream.close()
                    }
                } catch (ex: java.lang.Exception) {
                    take = false;
                    var reason = "Error"
                    cameraModel.insert(mlocation!!.longitude, mlocation!!.latitude, reason, "");
                    Log.e("carson", "Error:" + ex.message)
                }
            }

        }

//        mCamera?.autoFocus { success, camera ->
//            run {
//                mCamera?.takePicture(null, null, pictureCallback);
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        releaseCamera()
        handler.removeCallbacks(runnable);
    }
}