package com.maxcruz.permissions

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.maxcruz.reactivePermissions.ReactivePermissions
import com.maxcruz.reactivePermissions.entity.Permission

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val reactivePermissions: ReactivePermissions

    init {
        reactivePermissions = ReactivePermissions(this, REQUEST_CODE_PERMISSIONS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fineLocationPermission = Permission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.rationale_access_fine_location,
                false)
        val cameraPermission = Permission(
                Manifest.permission.CAMERA,
                R.string.rationale_camera,
                true)
        val readContactsPermission = Permission(
                Manifest.permission.READ_CONTACTS,
                null,
                true)
        val dangerousPermissions = listOf(
                fineLocationPermission,
                cameraPermission,
                readContactsPermission)

        Log.d("PERMISSION", "----------------------------------------------")
        reactivePermissions.observeResultPermissions().subscribe { event ->
            Log.d("PERMISSION", "${event.first} ${event.second}")
        }

        reactivePermissions.evaluate(dangerousPermissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS)
            reactivePermissions.receive(permissions, grantResults)
    }

}
