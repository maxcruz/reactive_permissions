# Reactive Permissions

[![License](https://img.shields.io/badge/license-Apache2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Repository](https://img.shields.io/badge/jcenter-1.0-brightgreen.svg)](https://bintray.com/maxcruz/maven/reactive-permissions)

Deal with Android M permissions in runtime in a simple way with reactive programming. This library was developed in [__Kotlin__](./README.md) but can [interoperate](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html) with [__Java__](./README-Java.md).

### Flow

[![App Flow](https://cdn.rawgit.com/MaxCruz/reactive_permissions/master/images/flow.svg)](./images/flow.svg)

### Example 

View an example in a simple activity [here](https://github.com/MaxCruz/reactive_permissions/tree/master/sample)

[IMAGE GIF GO HERE]

### Setup

Make sure you have configured the repository __jcenter()__, it is almost always there

Add this to the gradle dependencies for your module:
```gradle
compile 'com.github.maxcruz:reactive-permissions:1.1'
```
### Usage for Java, [read here](./README.md) for Kotlin implementation

Define permissions as needed:
```java
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
```

Create the library object for the request
```java
// Define a code to request the permissions
private static final int REQUEST_CODE = 10;
// Instantiate the library
private ReactivePermissions reactive = new ReactivePermissions(this, REQUEST_CODE);
```

Subscribe to observe results __Pair&lt;String, Boolean&gt;__
```java
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
```

Evaluate the defined permissions. Call __evaluate__ after of register the observer
```java
reactive.evaluate(permissions);
```

In the activity, receive the response from the user and pass to the lib
```java
@Override
public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
    if (code == REQUEST_CODE) {
        reactive.receive(permissions, results);
    }
}
```
