package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Category extends AppCompatActivity {
    CategoryAdapter listAdapter;
    ExpandableListView expListView;
    HashMap<String, List<String>> listDataChild;
    public ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<String> category,detail,categoryIcon;
    String selectedCategory,selectedSubcategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategory.php";
    private static final String DETAIL_URL = "http://vidcom.click/admin/android/viewDetailCategory.php?category=";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.categorySwipeRefreshLayout);
        spinner=(ProgressBar)findViewById(R.id.progressBarCategory);
        spinner.setVisibility(View.VISIBLE);

        category = new ArrayList<String>();
        detail = new ArrayList<String>();
        categoryIcon = new ArrayList<String>();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.categoryListview);
        //set refresh gesture when user swipe down
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipehomeresult", "onRefresh called from SwipeRefreshLayout");

                        // Signal SwipeRefreshLayout to start the progress indicator
                        swipeRefreshLayout.setRefreshing(true);

                        category.clear();
                        detail.clear();
                        categoryIcon.clear();
                        fetchCategory();
                    }
                }
        );

        //fetch class list asynchronously with volley
        fetchCategory();

        ImageButton btnSearch = (ImageButton)findViewById(R.id.btnSearchCategory);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Search.class);
                startActivity(i);
            }
        });
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackCategory);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnCategoryHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnCategoryProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnCategorySetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnCategoryNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvCategoryHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvCategoryProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvCategorySetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvCategoryNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    //GETTING CATEGORY LIST
    private void fetchCategory(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("homevolleyresult", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getJSONArray(JSON_ARRAY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);

                        Log.d("homevolleyresult", String.valueOf(result.length()));
                        // Parsing json
                        listDataChild = new HashMap<String, List<String>>();

                        for(int i=0; i<result.length(); i++) {
                            try{
                                JSONObject jsonObject = result.getJSONObject(i);

                                category.add(jsonObject.getString("category"));
                                categoryIcon.add(jsonObject.getString("category_icon"));
                                detail.add(jsonObject.getString("subcategory"));
                                JSONArray detailJSON = jsonObject.getJSONArray("subcategory"); //subcategory is an array that needs to be parsed again

                                List<String> listDetailJSON = new ArrayList<String>(); //so that detailJSON can be put into listDataChild
                                if(detailJSON.length() > 0) {
                                    for (int j = 0; j < detailJSON.length(); j++) {
                                        listDetailJSON.add(detailJSON.get(j).toString());
                                        //Log.v("detailjson", detailJSON.optString(j));
                                    }
                                }
                                listDataChild.put(category.get(i), listDetailJSON);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        listAdapter = new CategoryAdapter(Category.this, category, listDataChild, categoryIcon);
                        // setting list adapter
                        expListView.setAdapter(listAdapter);

                        // Listview Group click listener
                        expListView.setOnGroupClickListener(new OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                        int groupPosition, long id) {
                                // Toast.makeText(getApplicationContext(),
                                // "Group Clicked " + listDataHeader.get(groupPosition),
                                // Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });

                        // Listview Group expanded listener
                        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

                            @Override
                            public void onGroupExpand(int groupPosition) {
                                int len = listAdapter.getGroupCount();
                                for (int i = 0; i < len; i++) {
                                    if (i != groupPosition) {
                                        expListView.collapseGroup(i);
                                    }
                                }
                    /*Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Expanded",
                            Toast.LENGTH_SHORT).show();*/
                            }
                        });

                        // Listview Group collasped listener
                        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

                            @Override
                            public void onGroupCollapse(int groupPosition) {

                            }
                        });

                        // Listview on child click listener
                        expListView.setOnChildClickListener(new OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v,
                                                        int groupPosition, int childPosition, long id) {
                    /*Toast.makeText(getApplicationContext(),
                            listDataChild.get(
                                    category.get(groupPosition)).get(childPosition) + "",
                            Toast.LENGTH_SHORT).show();
                    */
                                selectedCategory = category.get(groupPosition);
                                selectedSubcategory = listDataChild.get(category.get(groupPosition)).get(childPosition).toString();
                                //String replaced = selectedCategory.replace(' ', '_');
                                String replaced = selectedSubcategory.replace("&", "%26").replace(' ', '_');
                                Log.v("categoryresult",DETAIL_URL+replaced);
                                getDetailCategoryJSON(DETAIL_URL+replaced);
                                return false;
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse errorRes = error.networkResponse;
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //Log.e("VolleyError",errorRes.toString());
                if(errorRes != null && errorRes.data != null){
                    //Log.e("VolleyError",errorRes.toString());
                }

                spinner.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        VolleyAppController.getInstance().addToRequestQueue(movieReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        spinner.setVisibility(View.GONE);
    }
    //END GETTING CATEGORY

    //GETTING CATEGORY DETAIL
    private void getDetailCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    return sb.toString().trim();
                }catch(Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                spinner.setVisibility(View.GONE);

                Log.v("categoryresult",s);
                if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("false")){
                    Toast.makeText(Category.this, "Class Not Available", Toast.LENGTH_LONG).show();
                    //Intent i = new Intent(getApplicationContext(), ClassEmpty.class);
                    //startActivity(i);
                }
                else{
                    Intent i = new Intent(getApplicationContext(), MentorList.class);
                    i.putExtra("jsonstring", s);
                    i.putExtra("category", selectedCategory);
                    startActivity(i);
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    //END GETTING CATEGORY DETAIL
}
