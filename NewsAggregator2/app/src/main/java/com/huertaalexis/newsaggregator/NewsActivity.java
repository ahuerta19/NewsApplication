package com.huertaalexis.newsaggregator;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.huertaalexis.newsaggregator.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewsActivity extends AppCompatActivity {

    private RequestQueue queue;
    private long start;
    private ActivityMainBinding binding;
    private final static String repURL = "https://newsapi.org/v2/top-headlines";
    private static final String yourAPIKey = "b855a922a69c4fe2b3d4484e16da1910";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        if(intent.hasExtra(Source.class.getName())){

            Source s = (Source) intent.getSerializableExtra(Source.class.getName());
                if (s == null)
                    return;

                setTitle(s.getName());
                Uri.Builder buildURL = Uri.parse(repURL).buildUpon();
                buildURL.appendQueryParameter("sources", s.getId());
                buildURL.appendQueryParameter("apiKey", yourAPIKey);
                String urlUsed = buildURL.build().toString();
                Log.d(TAG, "onCreate: " + urlUsed);


            }
        }

    private void doDownload() {
        //Build URL
        Uri.Builder buildURL = Uri.parse(repURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlUsed = buildURL.build().toString();

        //Used to debug URL
        //Log.d(TAG, "doDownload: " + urlUsed);

        //start = System.currentTimeMillis();


        Response.Listener<JSONObject> listener =
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //setTitle("Civil Advocacy");
                            //sourceList.clear();
                            JSONArray src = response.getJSONArray("sources");

                            for (int i = 0; i < src.length(); i++) {
                                JSONObject src2 = src.getJSONObject(i);
                                String id = null;
                                if(src2.has("id")) {
                                    id = src2.getString("id");
                                }
                                String name = null;
                                if(src2.has("name")) {
                                    name = src2.getString("name");
                                }
                                String cat = null;
                                if(src2.has("category")) {
                                    cat = src2.getString("category");
                                }

                                //sourceList.add(new Source(id, name, cat));
                            }
                            //updateData(sourceList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    setTitle("Duration: " + (System.currentTimeMillis() - start));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlUsed.toString(),
                        null, listener, error) { @Override
                public Map<String, String> getHeaders() throws AuthFailureError { Map<String, String> headers = new HashMap<>(); headers.put("User-Agent", "News-App");
                    return headers;
                } };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    }