// Volley service class for Approov Shapes App Demo
//
// MIT License
//
// Copyright (c) 2016-present, Critical Blue Ltd.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
// (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
// ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package io.approov.shapes;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
//import io.approov.service.volley.ApproovService;

public class VolleyService {
    private static Context appContext;

    // *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
    //private static ApproovService approovService;

    private static RequestQueue requestQueue;

    public static synchronized void initialize(Context context) {
        appContext = context;

        // *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
        //approovService = new ApproovService(appContext,
        //            appContext.getResources().getString(R.string.approov_config));
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // *** COMMENT THE LINE BELOW WITHOUT APPROOV ***
            requestQueue = Volley.newRequestQueue(appContext);

            // *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
            //requestQueue = Volley.newRequestQueue(appContext, approovService.getBaseHttpStack());
        }
        return requestQueue;
    }
}
