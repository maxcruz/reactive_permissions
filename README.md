# Reactive Permissions

[![License](https://img.shields.io/badge/license-Apache2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Repository](https://img.shields.io/badge/jcenter-1.0-brightgreen.svg)](https://bintray.com/maxcruz/maven/reactive-permissions)

Deal with Android M permissions in runtime in a simple way with reactive programming. This library was developed in __Kotlin__ but can [interoperate](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html) with Java.

### Flow

[DIAGRAM GO HERE]

### Example 

View an example in a simple activity [here](https://github.com/MaxCruz/reactive_permissions/tree/master/sample)

[IMAGE GIF GO HERE]

### Setup

Make sure that you have __jcenter()__ repository (usually it is already set) and just add this to the gradle dependencies for your module
```gradle
compile 'com.github.maxcruz:reactive-permissions:1.0'
```
### Usage

Define permissions as needed:
```kotlin
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

// Put al permissions that need request in a single array
val permissions = listOf(location, camera, contacts)
```


```
private val REQUEST_CODE_PERMISSIONS = 10

val reactivePermissions: ReactivePermissions
```

```
reactivePermissions = ReactivePermissions(this, REQUEST_CODE_PERMISSIONS)

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
```

```
reactivePermissions.observeResultPermissions().subscribe { event ->
  Log.d("PERMISSION", "${event.first} ${event.second}")
}
```

```
reactivePermissions.evaluate(dangerousPermissions)
```

```
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
  if (requestCode == REQUEST_CODE_PERMISSIONS)
    reactivePermissions.receive(permissions, grantResults)
}
```
