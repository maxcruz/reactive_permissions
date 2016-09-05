# Reactive Permissions

[![License](https://img.shields.io/badge/license-Apache2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Repository](https://img.shields.io/badge/jcenter-1.0-brightgreen.svg)](https://bintray.com/maxcruz/maven/reactive-permissions)

Deal with Android M permissions in runtime in a simple way with reactive programming. This library was developed in [__Kotlin__](./README.md) but can [interoperate](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html) with [__Java__](./README-Java.md).

- You may define the permissions that you need (one or many at once). Each permission must be set in the Manifest

- First verify if the permission is already granted, otherwise request to the user

- Each permission may contain a string resource explaining why your app requires this permission. If the user denied the permission, the explanation is displayed with the possibility to retry or skip. Explanation can be omitted if the context is clear.

- If some permissions are very important for your app functionality, you can define that can't continue without this. When an essential permission is denied, a special block dialog is displayed with retry or close options.

- If the user choose the option "never ask again", the retry button in the block dialog opens the preferences of the app.

### Flow

[![App Flow](https://cdn.rawgit.com/MaxCruz/reactive_permissions/master/images/flow.svg)](./images/flow.svg)

### Example 

View an example in a simple activity [here](./sample-java)

![](./images/show.gif)

### Setup

Make sure you have configured the repository __jcenter()__, it is almost always there

Add this to the gradle dependencies for your module:
```gradle
compile 'com.github.maxcruz:reactive-permissions:1.3'
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
### License
```
Copyright (C) 2016 Max Cruz
Copyright (C) 2007 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0.txt

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

