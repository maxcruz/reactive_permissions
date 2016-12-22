package com.maxcruz.permissions

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.maxcruz.reactivePermissions.ReactivePermissions
import com.maxcruz.reactivePermissions.entity.Permission

class MainFragment : Fragment() {

    // Define a code to request the permissions
    private val REQUEST_CODE = 10
    // Instantiate the library
    lateinit var reactive: ReactivePermissions

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        evaluatePermissions()
    }

    fun evaluatePermissions() {
        reactive = ReactivePermissions(this, REQUEST_CODE)
        val camera = Permission(
                Manifest.permission.CAMERA, // Permission constant to request
                R.string.rationale_camera, // String resource with rationale explanation
                true // Define if the app can continue without the permission
        )

        val location = Permission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.rationale_location,
                false // If the user deny this permission, block the app
        )


        val contacts = Permission(
                Manifest.permission.READ_CONTACTS,
                null, // The context is clear and isn't needed explanation for this permission
                true
        )

        // Put all permissions to evaluate in a single array
        val permissions = listOf(location, camera, contacts)

        // Subscribe to observe results
        reactive.observeResultPermissions().subscribe { event ->
            if (event.second) {
                Toast.makeText(activity, "${event.first} GRANTED :-)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "${event.first} DENIED :-(", Toast.LENGTH_SHORT).show()
            }
        }

        // Call for evaluate the permissions
        reactive.evaluate(permissions)
    }

    override fun onRequestPermissionsResult(code: Int, permissions: Array<String>, results: IntArray) {
        if (code == REQUEST_CODE)
            reactive.receive(permissions, results)
    }

}