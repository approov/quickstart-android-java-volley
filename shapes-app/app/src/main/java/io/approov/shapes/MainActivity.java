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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// *** UNCOMMENT THE LINE BELOW FOR APPROOV USING SECRETS PROTECTION ***
//import io.approov.service.volley.ApproovService;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Activity activity;
    private View statusView = null;
    private ImageView statusImageView = null;
    private TextView statusTextView = null;
    private Button helloCheckButton = null;
    private Button shapesCheckButton = null;

    private void sayHello() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getResources().getString(R.string.hello_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String message;
                        try {
                            message = response.getString("text");
                        }
                        catch (JSONException e) {
                            message = "JSONException: " + e.toString();
                        }
                        final String msg = message;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(R.drawable.hello);
                                statusTextView.setText(msg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Log.d(TAG, "Hello call failed: " + error.toString());
                         final int imgId = R.drawable.confused;
                         final String msg = "Request failed: " + error.toString();
                         activity.runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 statusImageView.setImageResource(imgId);
                                 statusTextView.setText(msg);
                                 statusView.setVisibility(View.VISIBLE);
                             }
                         });
                    }
                });
        VolleyService.getRequestQueue().add(request);
    }

    private void getShape() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getResources().getString(R.string.shapes_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Shapes call successful");
                        int imgId = R.drawable.confused;
                        String msg = "got shape";
                        try {
                            String shape = response.getString("shape");
                            if (shape != null) {
                                if (shape.equalsIgnoreCase("square")) {
                                    imgId = R.drawable.square;
                                } else if (shape.equalsIgnoreCase("circle")) {
                                    imgId = R.drawable.circle;
                                } else if (shape.equalsIgnoreCase("rectangle")) {
                                    imgId = R.drawable.rectangle;
                                } else if (shape.equalsIgnoreCase("triangle")) {
                                    imgId = R.drawable.triangle;
                                }
                            }
                        }
                        catch (JSONException e) {
                            msg = "JSONException: " + e.toString();
                        }
                        final int finalImgId = imgId;
                        final String finalMsg = msg;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(finalImgId);
                                statusTextView.setText(finalMsg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Shapes call failed: " + error.toString());
                        final int imgId = R.drawable.confused;
                        final String msg = "Request failed: " + error.toString();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(imgId);
                                statusTextView.setText(msg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Api-Key", getResources().getString(R.string.shapes_api_key));

                        // *** UNCOMMENT THE LINE BELOW FOR APPROOV USING SECRETS PROTECTION ***
                        //ApproovService.substituteHeader(headers, "Api-Key", null);

                        return headers;
                    }
        };
        VolleyService.getRequestQueue().add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // find controls
        statusView = findViewById(R.id.viewStatus);
        statusImageView = (ImageView) findViewById(R.id.imgStatus);
        statusTextView = findViewById(R.id.txtStatus);
        helloCheckButton = findViewById(R.id.btnConnectionCheck);
        shapesCheckButton = findViewById(R.id.btnShapesCheck);

        // handle hello connection check
        helloCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide status
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setVisibility(View.INVISIBLE);
                    }
                });
                sayHello();
            }
        });

        // handle getting shapes
        shapesCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide status
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setVisibility(View.INVISIBLE);
                    }
                });
                getShape();
            }
        });
    }
}
