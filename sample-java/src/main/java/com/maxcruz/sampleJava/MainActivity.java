package com.maxcruz.sampleJava;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.maxcruz.reactivePermissions.ReactivePermissions;
import com.maxcruz.reactivePermissions.entity.Permission;

import java.util.ArrayList;

import kotlin.Pair;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    // Define a code to request the permissions
    private static final int REQUEST_CODE = 10;
    // Instantiate the library
    private ReactivePermissions reactive = new ReactivePermissions(this, REQUEST_CODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission camera = new Permission(
                Manifest.permission.CAMERA, // Permission constant to request
                R.string.rationale_camera, // String resource with rationale explanation
                true // Define if the app can continue without the permission
        );

        Permission location = new Permission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.rationale_location,
                false // If the user deny this permission, block the app
        );

        Permission contacts = new Permission(
                Manifest.permission.READ_CONTACTS,
                null, // The context is clear and isn't needed explanation for this permission
                false
        );

        // Put all permissions to evaluate in a single array
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(camera);
        permissions.add(location);
        permissions.add(contacts);

        // Subscribe to observe results
        reactive.observeResultPermissions().subscribe(new Action1<Pair<String, Boolean>>() {

            @Override
            public void call(Pair<String, Boolean> event) {
                if (event.getSecond()) {
                    Toast.makeText(MainActivity.this, event.getFirst() + " GRANTED :-)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, event.getFirst() + " DENIED :-(", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // Call for evaluate the permissions
        reactive.evaluate(permissions);
    }

    // Receive the response from the user and pass to the lib
    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
        if (code == REQUEST_CODE) {
            reactive.receive(permissions, results);
        }
    }

}
