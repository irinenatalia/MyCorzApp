package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Wishlist extends AppCompatActivity {
    ListView lvWishlist;
    ArrayList<String> id,username,category,subcategory;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar spinner;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Intent intent = getIntent();
        myJSONString = intent.getStringExtra("WISHLIST_JSON");
        if(myJSONString.equalsIgnoreCase("") || myJSONString.equalsIgnoreCase("false")){
            spinner.setVisibility(View.GONE);
            TextView warning = (TextView)findViewById(R.id.warningWishlist);
            warning.setText("There is no available wishlist right now");
        }
        else{
            id = new ArrayList<String>();
            username = new ArrayList<String>();
            category = new ArrayList<String>();
            subcategory = new ArrayList<String>();

            // Parse JSON data to Listview
            extractJSON();
            showData();
        }
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackWishlist);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }
        });
    }
    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            result = jsonObject.getJSONArray(JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData(){
        try {
            //productList = new ArrayList<HashMap<String, String>>();
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                id.add(jsonObject.getString("id"));
                username.add(jsonObject.getString("username"));
                category.add(jsonObject.getString("category"));
                subcategory.add(jsonObject.getString("subcategory"));
                //Toast.makeText(Wishlist.this, username.get(i).toString(), Toast.LENGTH_SHORT).show();
            }

            lvWishlist =(ListView) findViewById(R.id.listWishlist);
            lvWishlist.setAdapter(new ListWishlist(this, username,category,subcategory));
            // Click event for single list row
            lvWishlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.v("wishlistclicked","clicked");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
