package com.buboslabwork.mycorz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassLocation extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyDAS5OFU7nXR0jiw0trJRCL--5VklfVHhw";
    private static ArrayList<HashMap<String, String>>  resultList;

    // for fetching place details
    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    LocationRequest mLocationRequest;
    GoogleMap googleMap;
    CameraUpdate cameraPosition,cameraZoom;
    Marker currLocationMarker;
    Boolean isFirstTimeLoadMap = true;

    Button setLocation;
    String location;
    String selectedLat = "";
    String selectedLong = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_location);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        // Getting reference to the SupportMapFragment of the activity_main.xml
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //google api client

        setLocation = (Button) findViewById(R.id.classLocationSet);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_suggestion_location));
        autoCompView.setOnItemClickListener(this);

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackClassLocation);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClassLocation.this.finish();
            }
        });
        setLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!location.isEmpty()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("returnedAddressParam", location);
                    resultIntent.putExtra("returnedLatParam", selectedLat);
                    resultIntent.putExtra("returnedLongParam", selectedLong);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                else
                    Toast.makeText(ClassLocation.this, "Pick your location first", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.v("mapsresult",resultList.get(position).get("place_id"));
        //Toast.makeText(this, resultList.get(position).get("place_id"), Toast.LENGTH_SHORT).show();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, resultList.get(position).get("place_id"));
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    public static ArrayList<HashMap<String, String>> autocomplete(String input) {
        //ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:id");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<HashMap<String, String>>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                HashMap<String, String> place = new HashMap<String, String>();
                String description = predsJsonArray.getJSONObject(i).getString("description");
                String placeId = predsJsonArray.getJSONObject(i).getString("place_id");
                place.put("description", description);
                place.put("place_id", placeId);
                resultList.add(place);
            }
            return resultList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return null;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<HashMap<String, String>> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            location = resultList.get(index).get("description");
            return resultList.get(index).get("description");
        }

        @Override
        public Filter getFilter() {
            final Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    // GOOGLE PLACES DETAILS & SHOWING MAP FUNCTIONS
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(isFirstTimeLoadMap) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MAPS_RECEIVE)
                    == PackageManager.PERMISSION_GRANTED) {
                this.googleMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(ClassLocation.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.MAPS_RECEIVE)
                        == PackageManager.PERMISSION_GRANTED) {
                    this.googleMap.setMyLocationEnabled(true);
                }
            }
            isFirstTimeLoadMap = false;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(ClassLocation.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("mapsresult", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            //googleMap.clear();
            final Place myPlace = places.get(0);
            LatLng queriedLocation = myPlace.getLatLng();
            selectedLat = String.valueOf(queriedLocation.latitude);
            selectedLong = String.valueOf(queriedLocation.longitude);

            //Toast.makeText(ClassLocation.this, String.valueOf(queriedLocation.latitude), Toast.LENGTH_SHORT).show();
            Log.v("mapsresult",String.valueOf(queriedLocation.latitude));
            LatLng point = new LatLng(queriedLocation.latitude, queriedLocation.longitude);

            cameraPosition = CameraUpdateFactory.newLatLng(point);
            cameraZoom = CameraUpdateFactory.zoomBy(14);

            // Showing the user input location in the Google Map
            googleMap.moveCamera(cameraPosition);
            //googleMap.animateCamera(cameraZoom);

            MarkerOptions options = new MarkerOptions();
            // Changing marker icon
            int height = 60;
            int width = 60;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_pushpin_map);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            options.position(point);
            options.title("Address");
            options.snippet(myPlace.getAddress().toString());

            // Adding the marker in the Google Map
            googleMap.addMarker(options).showInfoWindow();
            places.release();
        }
    };
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Google Places API connected.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MAPS_RECEIVE)
                == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //place marker at current position
                //mGoogleMap.clear();
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                MarkerOptions options = new MarkerOptions();
                // Changing marker icon
                int height = 60;
                int width = 60;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_pushpin_map);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                options.position(latLng);
                currLocationMarker = googleMap.addMarker(options);
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(3000); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        googleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        // Changing marker icon
        int height = 60;
        int width = 60;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_pushpin_map);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        options.position(latLng);
        currLocationMarker = googleMap.addMarker(options);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        //If you only need one location, unregister the listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }
}
