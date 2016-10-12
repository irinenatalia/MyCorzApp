package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MentorList extends AppCompatActivity implements OnMapReadyCallback {
    private SlidingUpPanelLayout slidingLayout;
    ListView lvMentor;
    ArrayList<String> profilePicture,category,username,completeName,rating,location,latitude,longitude,markerID;

    public JSONArray result = null;
    public String myJSONString, categoryTitle;
    private static final String JSON_ARRAY ="result";

    User user;
    String email;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    GoogleMap googleMap;
    Boolean flagZoomMap = true;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_list);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mentorListMap)).getMapAsync(this);

        user=PrefUtils.getCurrentUser(MentorList.this);
        if(user != null){
            email = user.email;
        }

        Intent intent = getIntent();
        myJSONString = intent.getStringExtra("jsonstring");
        categoryTitle = intent.getStringExtra("category");

        TextView toolbarTitle = (TextView)findViewById(R.id.mentorListToolbarTitle);
        toolbarTitle.setText("Category "+categoryTitle);
        lvMentor =(ListView) findViewById(R.id.lvMentorList);
        profilePicture = new ArrayList<String>();
        completeName = new ArrayList<String>();
        username = new ArrayList<String>();
        category = new ArrayList<String>();
        rating = new ArrayList<String>();
        location = new ArrayList<String>();
        latitude = new ArrayList<String>();
        longitude = new ArrayList<String>();
        markerID = new ArrayList<String>();

        if(googleMap != null) {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {
                    //if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                    Toast.makeText(MentorList.this, "clicked " + arg0.getId(), Toast.LENGTH_SHORT).show();// display toast
                    return true;
                }
            });
        }

        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //slidingLayout.setDragView(lvMentor);
        //slidingLayout.setEnableDragViewTouchEvents(true);
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            // During the transition of expand and collapse onPanelSlide function will be called.
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                Log.e("panelresult", "onPanelSlide, offset " + slideOffset);
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("panelresult", "onPanelStateChanged " + newState);
            }

            // Called when secondary layout is dragged up by user
            //@Override
            public void onPanelExpanded(View panel) {

                Log.e("panelresult", "onPanelExpanded");
            }

            // Called when secondary layout is dragged down by user
            //@Override
            public void onPanelCollapsed(View panel) {

                Log.e("panelresult", "onPanelCollapsed");
            }

            //@Override
            public void onPanelAnchored(View panel) {

                Log.e("panelresult", "onPanelAnchored");
            }

            //@Override
            public void onPanelHidden(View panel) {

                Log.e("panelresult", "onPanelHidden");
            }
        });

        //have to set listview in OnCreate, or else the click listener won't work
        lvMentor.setAdapter(new ListAvailableMentor(this, category,username,completeName,rating,profilePicture));
        // Click event for single list row
        lvMentor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v("homeresult mentor", username.get(position));
                Log.v("homeresult email", email);

                if(username.get(position).equalsIgnoreCase(email)){
                    Intent i = new Intent(getApplicationContext(), MentorDetailClassAsMentor.class);
                    i.putExtra("mentorUsername",username.get(position));
                    i.putExtra("pageSource","MentorList");
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getApplicationContext(), MentorDetailClass.class);
                    i.putExtra("mentorUsername",username.get(position));
                    i.putExtra("pageSource","MentorList");
                    startActivity(i);
                }
            }
        });
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackMentorList);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Category.class);
                startActivity(i);
                //finish();
            }
        });
        ImageButton btnSearch = (ImageButton)findViewById(R.id.btnSearchMentorList);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Search.class);
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
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                profilePicture.add(jsonObject.getString("profile_picture"));
                completeName.add(jsonObject.getString("complete_name"));
                username.add(jsonObject.getString("mentor_username"));
                category.add(jsonObject.getString("category"));
                rating.add(jsonObject.getString("rating"));
                location.add(jsonObject.getString("location"));
                latitude.add(jsonObject.getString("lat"));
                longitude.add(jsonObject.getString("long"));

                Log.v("mentorlist lat", latitude.get(i));
                Log.v("mentorlist long", longitude.get(i));

                LatLng latLng = new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i)));

                if(googleMap!=null) {
                    MarkerOptions options = new MarkerOptions();
                    // Changing marker icon
                    int height = 60;
                    int width = 60;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_pushpin_map);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    options.position(latLng);
                    //googleMap.addMarker(options);
                    Marker mkr = googleMap.addMarker(options);
                    markerID.add(mkr.getId());

                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            //if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                            Toast.makeText(MentorList.this, "clicked " + arg0.getId(), Toast.LENGTH_SHORT).show();// display toast
                            return true;
                        }
                    });

                    if (flagZoomMap == true) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        flagZoomMap = false;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mGoogleApiClient = new GoogleApiClient.Builder(MentorList.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();


        extractJSON();
        showData();


    }
}
