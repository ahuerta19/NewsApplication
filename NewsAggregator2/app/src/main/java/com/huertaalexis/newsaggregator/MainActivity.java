package com.huertaalexis.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.huertaalexis.newsaggregator.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout background;

    private final static String repURL = "https://newsapi.org/v2/sources";
    private static final String yourAPIKey = "b855a922a69c4fe2b3d4484e16da1910";
    private static final String TAG = "MainActivity";

    private final static String artURL = "https://newsapi.org/v2/top-headlines";

    private RequestQueue queue;
    private long start;

    DrawerLayout drawerLay;

    private final ArrayList<Source> sourceList = new ArrayList<>();
    private final ArrayList<Source> aList = new ArrayList<>();
    private ArrayList<Source> allList = new ArrayList<>();
    private final HashMap<String, ArrayList<Source>> sourceData = new HashMap<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ConstraintLayout mConstraintLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<Source> arrayAdapter;

    private ActivityMainBinding binding;
    private boolean newsGate = true;

    private String newsArt;

    private ArticleAdapter articleAdapter;
    private final ArrayList<Article> articleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDrawerLayout = binding.drawerLayout;
        mConstraintLayout = binding.cLayout;
        mDrawerList = binding.leftDrawer;
        queue = Volley.newRequestQueue(this);
        drawerLay = binding.drawerLayout;
        articleAdapter = new ArticleAdapter(this, articleList);

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Source c = sourceList.get(position);
                    newsArt = c.getId();
                    newsGate = false;
                    setTitle(c.getName());
                    ViewPager2 viewPager2 = binding.viewPager;
                    viewPager2.setAdapter(articleAdapter);
                    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    drawerLay.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    doArticle();
                    mDrawerLayout.closeDrawer(mConstraintLayout);
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        aList.clear();
        doDownload();
    }
    long timeinMillisEpoch=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formatDate(String zulu){
        Log.d(TAG, "formatDate: " + zulu);
        if(zulu.contains("Z")) {
            Instant instant = Instant.parse(zulu);
            timeinMillisEpoch = instant.toEpochMilli();
        }else{
            String s = zulu;
            s = s.replace("+00:00", "Z");
            Log.d(TAG, "formatDate: " + s);
            Instant instant = Instant.parse(s);
            timeinMillisEpoch = instant.toEpochMilli();
        }

        Date dateTime = new Date(timeinMillisEpoch); // Java time values need milliseconds
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault());
        String timeStr = timeFormat.format(dateTime);
        return timeStr;
    }


    public void updateData2(ArrayList<Article> artList){
        articleAdapter.notifyItemRangeChanged(0, artList.size());
    }

    public void updateData(ArrayList<Source> listIn) {
        for (Source s : listIn) {
            if (!sourceData.containsKey(s.getCategory())) {
                sourceData.put(s.getCategory(), new ArrayList<>());
            }
            ArrayList<Source> slist = sourceData.get(s.getCategory());
            if (slist != null) {
                slist.add(s);
            }
        }
        sourceData.put("All", listIn);


        ArrayList<String> tempList = new ArrayList<>(sourceData.keySet());
        Collections.sort(tempList);
        for (String st : tempList)
            opt_menu.add(st);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceList);
        mDrawerList.setAdapter(arrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        sourceList.clear();
        ArrayList<Source> clist = sourceData.get(item.getTitle().toString());
        if (clist != null) {
            sourceList.addAll(clist);
            if(newsGate == true) {
                length = sourceList.size();
                setTitle("News Gateway (" + length + ")");
            }
        }
        if (item.toString().equals("All")){
            sourceList.addAll(aList);
            length = sourceList.size();
            if(newsGate == true) {
                setTitle("News Gateway (" + length + ")");
                Log.d(TAG, "onOptionsItemSelected: " + item.toString());
            }
        }
        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }

    public int length;
    private void doDownload() {
            //Build URL
            Uri.Builder buildURL = Uri.parse(repURL).buildUpon();
            buildURL.appendQueryParameter("apiKey", yourAPIKey);
            String urlUsed = buildURL.build().toString();

        //Used to debug URL
            //Log.d(TAG, "doDownload: " + urlUsed);

            start = System.currentTimeMillis();


            Response.Listener<JSONObject> listener =
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //setTitle("Civil Advocacy");
                                sourceList.clear();
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

                                    sourceList.add(new Source(id, name, cat));
                                }
                                updateData(sourceList);
                                for (Source source : sourceList) {
                                    aList.add(source);
                                    Log.d(TAG, "OK" + aList.size());
                                }
                                length = sourceList.size();
                                setTitle("News Gateway (" + length + ")");                                //setTitle("omg " + " " + length);
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


    private void doArticle() {
        //Build URL
        Uri.Builder buildURL = Uri.parse(artURL).buildUpon();
        buildURL.appendQueryParameter("sources", newsArt);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlUsed = buildURL.build().toString();
        Log.d(TAG, "onCreate: " + urlUsed);


        //Used to debug URL
        //Log.d(TAG, "doDownload: " + urlUsed);

        start = System.currentTimeMillis();


        Response.Listener<JSONObject> listener =
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //setTitle("Civil Advocacy");
                            articleList.clear();
                            articleAdapter.notifyDataSetChanged();
                            JSONArray src3 = response.getJSONArray("articles");

                            for (int i = 0; i < src3.length(); i++) {
                                JSONObject src4 = src3.getJSONObject(i);
                                String title = null;
                                if(src4.has("title")) {
                                    title = src4.getString("title");
                                }
                                String date = null;
                                String newDate = null;
                                if(src4.has("publishedAt")) {
                                    date = src4.getString("publishedAt");
                                    newDate = formatDate(date);
                                }
                                String link = null;
                                if(src4.has("url")){
                                    link = src4.getString("url");
                                }

                                String author = null;
                                if(src4.has("author")) {
                                    author = src4.getString("author");
                                }
                                String urlImg = null;
                                if(src4.has("urlToImage")) {
                                    urlImg = src4.getString("urlToImage");
                                }
                                String desc = null;
                                if(src4.has("description")) {
                                    desc = src4.getString("description");
                                }
                                int count = src3.length();
                                String cnt = String.valueOf(count);
                                articleList.add(new Article(title, newDate, author, urlImg,desc,cnt,link));
                            }
                            Log.d(TAG, "doArticle: " + articleList.size());
                            updateData2(articleList);
                        } catch (JSONException /*| ParseException*/ e) {
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
