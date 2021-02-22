# Approov Quickstart: Android Java Volley

This quickstart is written specifically for native Android apps that are written in Java and use [`Volley`](https://developer.android.com/training/volley) for making the API calls that you wish to protect with Approov. If this is not your situation then check if there is a more relevant Quickstart guide available.

## WHAT YOU WILL NEED
* Access to a trial or paid Approov account
* The `approov` command line tool [installed](https://approov.io/docs/latest/approov-installation/) with access to your account
* [Android Studio](https://developer.android.com/studio) installed (version 4.1.2 is used in this guide)
* The contents of the folder containing this README

## WHAT YOU WILL LEARN
* How to integrate Approov into a real app in a step by step fashion
* How to register your app to get valid tokens from Approov
* A solid understanding of how to integrate Approov into your own app that uses Java and [Volley](https://developer.android.com/training/volley) to make API calls
* Some pointers to other Approov features

## RUNNING THE SHAPES APP WITHOUT APPROOV

Open the project in the `shapes-app` folder using `File->Open` in Android Studio.

Ensure that the currently active build variant is `debug` and then build an APK as follows:

![Build APK](readme-images/build-apk.png)

The following dialog will popup when the build is complete:

![Build APK Result](readme-images/build-apk-result.png)

Click on `locate` to get the `app-debug.apk` location and a file explorer window will open. Now you need to open the terminal of your operating system on this location and execute:
```
$ adb install app-debug.apk
```
> **NOTE**:
>   * The [`adb`](https://developer.android.com/studio/command-line/adb) tool needs to be installed in in your `$PATH`.
>   * The mobile device needs to be in Developer mode, and USB debugging needs to be [enabled](https://developer.android.com/studio/command-line/adb#Enabling) and set to always trust in your device.

From the mobile device launch the app and you will see two buttons:

<p>
    <img src="readme-images/app-startup.png" width="256" title="Shapes App Startup">
</p>

Click on the `Say Hello` button and you should see this:

<p>
    <img src="readme-images/hello-okay.png" width="256" title="Hello Okay">
</p>

This checks the connectivity by connecting to the endpoint `https://shapes.approov.io/v1/hello`. Now press the `Get Shape` button and you will see this:

<p>
    <img src="readme-images/shapes-bad.png" width="256" title="Shapes Bad">
</p>

This contacts `https://shapes.approov.io/v2/shapes` to get the name of a random shape. It gets the status code 400 (`Bad Request`) because this endpoint is protected with an Approov token. Next, you will add Approov into the app so that it can generate valid Approov tokens and get shapes.

## ADD THE APPROOV DEPENDENCY

The Approov integration is available via [`jitpack`](https://jitpack.io). This allows inclusion into the project by simply specifying a dependency in the `gradle` files for the app. Firstly, `jitpack` needs to be added as follows to the end the `repositories` section in the `build.gradle` file at the top level of the project:

![Project Build Gradle](readme-images/root-gradle.png)

The `approov-service-volley` dependency needs to be added as follows to the `build.gradle` at the app level:

![App Build Gradle](readme-images/app-gradle.png)

Note that in this case the dependency has been added with the tag `main-SNAPSHOT`. This gets the latest version. However, for your projects we recommend you add a dependency to a specific version. You can see the latest in the `README` at [`approov-service-volley`](https://github.com/approov/approov-service-volley).

Note that `approov-service-volley` is actually an open source wrapper layer that allows you to easily use Approov with `Volley`. This has a further dependency to the closed source Approov SDK itself.

## ENSURE THE SHAPES API IS ADDED

In order for Approov tokens to be generated for `https://shapes.approov.io/v2/shapes` it is necessary to inform Approov about it. If you are using a demo account this is unnecessary as it is already setup. For a trial account do:
```
$ approov api -add shapes.approov.io
```
Tokens for this domain will be automatically signed with the specific secret for this domain, rather than the normal one for your account.

## SETUP YOUR APPROOV CONFIGURATION

The Approov SDK needs a configuration string to identify the account associated with the app. Obtain it using:
```
$ approov sdk -getConfig initial-config.txt
```
Copy and paste the `initial-config.txt` file content into a new created `approov_config` string resource entry as shown (the actual entry will be much longer than shown).

![Initial Config String](readme-images/initial-config.png)

The app reads this string to initialize the Approov SDK.

## MODIFY THE APP TO USE APPROOV

Uncomment the various lines needed to use Approov in `VolleyService`. There is also one line that needs to be commented out. You should have the following:

![Approov Volley Service](readme-images/approov-volley-service.png)

The `VolleyService` provides a singleton `RequestQueue` that is created on demand. This is a typical pattern for using `Volley`. The changes shown here cause the request queue to be constructed with a specialized `BaseHttpStack` that automatically adds Approov tokens to the requests and pins the connections to ensure that no Man-in-the-Middle can eavesdrop on any communication being made.

The initialization is also extended to initialize the underlying `Approov` SDK with the configuration string added earlier.

## REGISTER YOUR APP WITH APPROOV

In order for Approov to recognize the app as being valid it needs to be registered with the service. Ensure that the currently active build variant is `debug` and then build an APK as follows:

![Build APK](readme-images/build-apk.png)

The following dialog will popup when the build is complete:

![Build APK Result](readme-images/build-apk-result.png)

Click on `locate` to get the `app-debug.apk` location. Register the app with Approov:
```
$ approov registration -add app-debug.apk
```

## RUNNING THE SHAPES APP WITH APPROOV

Install the `app-debug.apk` that you just registered on the device. You will need to remove the old app from the device first.
```
$ adb install -r app-debug.apk
```
Launch the app and press the `Get Shape` button. You should now see this (or another shape):

<p>
    <img src="readme-images/shapes-good.png" width="256" title="Shapes Good">
</p>

This means that the app is getting a validly signed Approov token to present to the shapes endpoint.

## WHAT IF I DON'T GET SHAPES

If you still don't get a valid shape then there are some things you can try. Remember this may be because the device you are using has some characteristics that cause rejection for the currently set [Security Policy](https://approov.io/docs/latest/approov-usage-documentation/#security-policies) on your account:

* Ensure that the version of the app you are running is exactly the one you registered with Approov. Also, if you are running the app from a debugger then valid tokens are not issued.
* Look at the [`logcat`](https://developer.android.com/studio/command-line/logcat) output from the device. Information about any Approov token fetched or an error is output at the `INFO` level, e.g. `2020-02-10 13:55:55.774 10442-10705/io.approov.shapes I/ApproovInterceptor: Approov Token for shapes.approov.io: {"did":"+uPpGUPeq8bOaPuh+apuGg==","exp":1581342999,"ip":"1.2.3.4","sip":"R-H_vE"}`. You can easily [check](https://approov.io/docs/latest/approov-usage-documentation/#loggable-tokens) the validity and find out any reason for a failure.
* Consider using an [Annotation Policy](https://approov.io/docs/latest/approov-usage-documentation/#annotation-policies) during initial development to directly see why the device is not being issued with a valid token.
* Use `approov metrics` to see [Live Metrics](https://approov.io/docs/latest/approov-usage-documentation/#live-metrics) of the cause of failure.
* Running the app on an emulator will not provide valid Approov tokens. You will need to `whitelist` the device.
* You can use a debugger or emulator and get valid Approov tokens on a specific device by [whitelisting](https://approov.io/docs/latest/approov-usage-documentation/#adding-a-device-security-policy). As a shortcut, when you are first setting up, you can add a [device security policy](https://approov.io/docs/latest/approov-usage-documentation/#adding-a-device-security-policy) using the `latest` shortcut as discussed so that the `device ID` doesn't need to be extracted from the logs or an Approov token.

## NEXT STEPS

This quickstart guide has taken you through the steps of adding Approov to the shapes demonstration app. If you have you own app using `Volley` you can follow exactly the same steps to add Approov. See [Approov Service Volley](https://github.com/approov/approov-service-volley) for additional information on integration.

Now you might want to explore some other Approov features:

* Managing your app [registrations](https://approov.io/docs/latest/approov-usage-documentation/#managing-registrations)
* Manage the [pins](https://approov.io/docs/latest/approov-usage-documentation/#public-key-pinning-configuration) on the API domains to ensure that no Man-in-the-Middle attacks on your app's communication are possible.
* Update your [Security Policy](https://approov.io/docs/latest/approov-usage-documentation/#security-policies) that determines the conditions under which an app will be given a valid Approov token.
* Learn how to [Manage Devices](https://approov.io/docs/latest/approov-usage-documentation/#managing-devices) that allows you to change the policies on specific devices.
* Understand how to provide access for other [Users](https://approov.io/docs/latest/approov-usage-documentation/#user-management) of your Approov account.
* Use the [Metrics Graphs](https://approov.io/docs/latest/approov-usage-documentation/#metrics-graphs) to see live and accumulated metrics of devices using your account and any reasons for devices being rejected and not being provided with valid Approov tokens. You can also see your billing usage which is based on the total number of unique devices using your account each month.
* Use [Service Monitoring](https://approov.io/docs/latest/approov-usage-documentation/#service-monitoring) emails to receive monthly (or, optionally, daily) summaries of your Approov usage.
* Consider using [Token Binding](https://approov.io/docs/latest/approov-usage-documentation/#token-binding).
* Learn about [automated approov CLI usage](https://approov.io/docs/latest/approov-usage-documentation/#automated-approov-cli-usage).
* Investigate other advanced features, such as [Offline Security Mode](https://approov.io/docs/latest/approov-usage-documentation/#offline-security-mode), [SafetyNet Integration](https://approov.io/docs/latest/approov-usage-documentation/#google-safetynet-integration) and [Android Automated Launch Detection](https://approov.io/docs/latest/approov-usage-documentation/#android-automated-launch-detection).