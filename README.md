# Approov Quickstart: Android Java Volley

This quickstart is written specifically for native Android apps that are written in Java and use [`Volley`](https://developer.android.com/training/volley) for making the API calls that you wish to protect with Approov. If this is not your situation then check if there is a more relevant Quickstart guide available.

This page provides all the steps for integrating Approov into your app. Additionally, a step-by-step tutorial guide using our [Shapes App Example](https://github.com/approov/quickstart-android-java-volley/blob/master/SHAPES-EXAMPLE.md) is also available.

To follow this guide you should have received an onboarding email for a trial or paid Approov account.

## ADDING APPROOV SERVICE DEPENDENCY
The Approov integration is available via [`maven`](https://mvnrepository.com/repos/central). This allows inclusion into the project by simply specifying a dependency in the `gradle` files for the app.
The `Maven` repository is already present in the gradle.build file so the only import you need to make is the actual service layer itself:

```
implementation("io.approov:service.volley:3.3.0")
```

Make sure you do a Gradle sync (by selecting `Sync Now` in the banner at the top of the modified `.gradle` file) after making these changes.

This package is actually an open source wrapper layer that allows you to easily use Approov with `Volley`. This has a further dependency to the closed source [Approov SDK](https://central.sonatype.com/artifact/io.approov/approov-android-sdk/3.3.0).

## MANIFEST CHANGES
The following app permissions need to be available in the manifest to use Approov:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

Note that the minimum SDK version you can use with the Approov package is 21 (Android 5.0). 

Please [read this](https://approov.io/docs/latest/approov-usage-documentation/#targeting-android-11-and-above) section of the reference documentation if targeting Android 11 (API level 30) or above.

## INITIALIZING APPROOV SERVICE
In order to use the `ApproovService` you should create a `VolleyService` class:

```Java
import io.approov.service.volley.ApproovService;

public class VolleyService {
    private static Context appContext;
    private static RequestQueue requestQueue;

    public static synchronized void initialize(Context context) {
        appContext = context;
        ApproovService.initialize(appContext, "<enter-your-config-string-here>")
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(appContext, ApproovService.getBaseHttpStack());
        }
        return requestQueue;
    }
}
```

The `<enter-your-config-string-here>` is a custom string that configures your Approov account access. This will have been provided in your Approov onboarding email.

You must initialize this when your app is created, usually in the `onCreate` method:

```Java
public class YourApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyService.initialize(getApplicationContext());
    }
}
```

You can then make Approov enabled `Volley` API calls by using the `RequestQueue` available from the `VolleyService`:

```Java
VolleyService.getRequestQueue().add(request);
```

This uses the `ApproovService` base `http` stack to include an interceptor to add the `Approov-Token` header and pins the connections.

Approov errors will generate an `ApproovException`, which is a type of volley `AuthFailureError`. This may be further specialized into an `ApproovNetworkException`, indicating an issue with networking that should provide an option for a user initiated retry.

## CHECKING IT WORKS
Initially you won't have set which API domains to protect, so the interceptor will not add anything. It will have called Approov though and made contact with the Approov cloud service. You will see logging from Approov saying `UNKNOWN_URL`.

Your Approov onboarding email should contain a link allowing you to access [Live Metrics Graphs](https://approov.io/docs/latest/approov-usage-documentation/#metrics-graphs). After you've run your app with Approov integration you should be able to see the results in the live metrics within a minute or so. At this stage you could even release your app to get details of your app population and the attributes of the devices they are running upon.

## NEXT STEPS
To actually protect your APIs and/or secrets there are some further steps. Approov provides two different options for protection:

* [API PROTECTION](https://github.com/approov/quickstart-android-java-volley/blob/master/API-PROTECTION.md): You should use this if you control the backend API(s) being protected and are able to modify them to ensure that a valid Approov token is being passed by the app. An [Approov Token](https://approov.io/docs/latest/approov-usage-documentation/#approov-tokens) is short lived crytographically signed JWT proving the authenticity of the call.

* [SECRETS PROTECTION](https://github.com/approov/quickstart-android-java-volley/blob/master/SECRETS-PROTECTION.md): This allows app secrets, including API keys for 3rd party services, to be protected so that they no longer need to be included in the released app code. These secrets are only made available to valid apps at runtime.

Note that it is possible to use both approaches side-by-side in the same app.

See [REFERENCE](https://github.com/approov/quickstart-android-java-volley/blob/master/REFERENCE.md) for a complete list of all of the `ApproovService` methods.
