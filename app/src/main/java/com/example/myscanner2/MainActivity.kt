package com.example.myscanner2

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.myscanner2.Fragments.StartupFragment
import com.example.myscanner2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var context: Context

    private var readPermissionGranted=false
    private var writePermissionGranted=false
    private var cameraPermissionGranted=false
    private lateinit var permissionLauncher:ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission(), ActivityResultCallback {})

        context=this
        var startupFragment= StartupFragment()


        supportFragmentManager.beginTransaction().apply{
            this.replace(R.id.mainFl,startupFragment)
            this.commit()
        }
    }

    fun updateOrRequestPermissions(){
        var hasReadPermissions=ContextCompat.checkSelfPermission(context,android.Manifest.permission
                .READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

        var hasCameraPermissions=ContextCompat.checkSelfPermission(context,android.Manifest.permission
                .CAMERA)==PackageManager.PERMISSION_GRANTED

        var hasWritePermissions=ContextCompat.checkSelfPermission(context,android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

        var minSdk=Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q

        readPermissionGranted=hasReadPermissions
        cameraPermissionGranted=hasCameraPermissions
        writePermissionGranted=hasWritePermissions||minSdk

        if(!readPermissionGranted){
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(!writePermissionGranted){
            permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(!cameraPermissionGranted){
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}