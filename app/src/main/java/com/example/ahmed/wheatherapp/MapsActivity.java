package com.example.ahmed.wheatherapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Spinner.OnItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    final static String FIRST_TIME = "FIRST_TIME";
    final static int MODE = Context.MODE_PRIVATE;
    String url = null;
    Spinner spinnerCountriesNames = null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    String LOGGED_OR_NOT = "LOGGED_OR_NOT";
    Location mLastKnownLocation = null;
    boolean mLocationPermissionGranted = true;
    float DEFAULT_ZOOM = 10f;
    MarkerOptions markerOptions = new MarkerOptions();
    FusedLocationProviderClient mFusedLocationProviderClient = null;


//    //private void showCurrentPlace() {
//        if (mMap == null) {
//            return;
//        }
//
//        if (mLocationPermissionGranted) {
//            // Get the likely places - that is, the businesses and other points of interest that
//            // are the best match for the device's current location.
//            @SuppressWarnings("MissingPermission") final
//            Task<PlaceLikelihoodBufferResponse> placeResult =
//                    mPlaceDetectionClient.getCurrentPlace(null);
//            placeResult.addOnCompleteListener
//                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//
//                                // Set the count, handling cases where less than 5 entries are returned.
//                                int count;
//                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
//                                    count = likelyPlaces.getCount();
//                                } else {
//                                    count = M_MAX_ENTRIES;
//                                }
//
//                                int i = 0;
//                                mLikelyPlaceNames = new String[count];
//                                mLikelyPlaceAddresses = new String[count];
//                                mLikelyPlaceAttributions = new String[count];
//                                mLikelyPlaceLatLngs = new LatLng[count];
//
//                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                                    // Build a list of likely places to show the user.
//                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
//                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
//                                            .getAddress();
//                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
//                                            .getAttributions();
//                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
//
//                                    i++;
//                                    if (i > (count - 1)) {
//                                        break;
//                                    }
//                                }
//
//                                // Release the place likelihood buffer, to avoid memory leaks.
//                                likelyPlaces.release();
//
//                                // Show a dialog offering the user the list of likely places, and add a
//                                // marker at the selected place.
//                                openPlacesDialog();
//
//                            } else {
//                                Log.e(TAG, "Exception: %s", task.getException());
//                            }
//                        }
//                    });
//        } else {
//            // The user has not granted permission.
//            Log.i(TAG, "The user did not grant location permission.");
//
//            // Add a default marker, because the user hasn't selected a place.
//            mMap.addMarker(new MarkerOptions()
//                    .title(getString(R.string.default_info_title))
//                    .position(mDefaultLocation)
//                    .snippet(getString(R.string.default_info_snippet)));
//
//            // Prompt the user for permission.
//            getLocationPermission();
//        }
//    }

    //    private void openPlacesDialog() {
//        // Ask the user to choose the place where they are now.
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // The "which" argument contains the position of the selected item.
//                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
//                String markerSnippet = mLikelyPlaceAddresses[which];
//                if (mLikelyPlaceAttributions[which] != null) {
//                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
//                }
//
//                // Add a marker for the selected place, with an info window
//                // showing information about that place.
//                mMap.addMarker(new MarkerOptions()
//                        .title(mLikelyPlaceNames[which])
//                        .position(markerLatLng)
//                        .snippet(markerSnippet));
//
//                // Position the map's camera at the location of the marker.
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
//                        DEFAULT_ZOOM));
//            }
//        };
//
//        // Display the dialog.
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(R.string.pick_place)
//                .setItems(mLikelyPlaceNames, listener)
//                .show();
//    }
    CheckBox cbPlacePicker, cbGoogleMapArea = null;
    int PLACE_PICKER_REQUEST = 1;
    RelativeLayout rlMainContaier = null;
    AlertDialog alertDialog = null;
    AlertDialog.Builder builder = null;
    String nameOfCurrentLocation = "";
    String pressure = "", humidity = "", temp = "", desc = "", dt = "", country = "";
    String sunset = "", sunrise = "", id = "";
    Place place = null;
    ScrollView sv = null;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=958b79f74fc722a728672ba728785e74&lat=30.044420&lon=31.235712&units=metric
        //http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=958b79f74fc722a728672ba728785e74&q=cairo&units=metric
        //http://api.openweathermap.org/data/2.5/weather?id=524901&APPID=958b79f74fc722a728672ba728785e74&q=Canberra&units=metric
        //http://api.openweathermap.org/data/2.5/weather?id=524901&APPID=958b79f74fc722a728672ba728785e74&lat=30.044420&lon=31.235712
        url = "http://api.openweathermap.org/data/2.5/weather?id=524901&APPID=958b79f74fc722a728672ba728785e74";
        init();
        //fetchData(url);


        sharedPreferences = getSharedPreferences(FIRST_TIME, MODE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains(LOGGED_OR_NOT)) {
            //if (sharedPreferences.getString(LOGGED_OR_NOT, "") == "logged") {
            findViewById(R.id.llFun).setVisibility(View.GONE);
            //}
        } else {
            final Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_to_right);
            findViewById(R.id.llFun).findViewById(R.id.btnOkThatisFun).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.llFun).startAnimation(animation1);
                    findViewById(R.id.llFun).setVisibility(View.GONE);
                    editor.putString(LOGGED_OR_NOT, "logged").commit();
                }
            });


        }


    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        editor.putString(LOGGED_OR_NOT, "logged").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (sharedPreferences.contains(LOGGED_OR_NOT)) {
//            if (sharedPreferences.getString(LOGGED_OR_NOT, "") == "logged") {
//                findViewById(R.id.tvFun).setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (mMap == null)
            return;

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);

//        } else {
//            // Show rationale and request permission.
//        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                cbGoogleMapArea.setChecked(true);
                cbPlacePicker.setChecked(false);
            }
        });

        getDeviceLocation();


    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()/* && task.isComplete()*/) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation != null) {
                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                mMap.addMarker(markerOptions.position(latLng).title("I'm here !!! "));
                            }

                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
                            LatLng sydney = new LatLng(-34, 151);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void init() {
//        Locale[] locales = Locale.getAvailableLocales();
//        ArrayList<String> countries = new ArrayList<String>();
//        String country;
//        for (Locale locale : locales) {
//            country = locale.getDisplayCountry();
//            if (country.length() > 0 && !countries.contains(country)) {
//                countries.add(country);
//            }
//        }
//        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
//
//        spinnerCountriesNames = (Spinner) findViewById(R.id.spinnerCountriesNames);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
//        spinnerCountriesNames.setAdapter(adapter);
//        spinnerCountriesNames.setOnItemSelectedListener(this);
        /////////////////////////////////////////////////////////////

        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//         Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//         Construct a FusedLocationProviderClient.
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        cbPlacePicker = (CheckBox) findViewById(R.id.cbPickPlace);
        cbGoogleMapArea = (CheckBox) findViewById(R.id.cbGoogleMapArea);
        rlMainContaier = (RelativeLayout) findViewById(R.id.rlMainContainer);

        sv = (ScrollView) findViewById(R.id.sv);
        findViewById(R.id.svContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbGoogleMapArea.setChecked(false);
                cbPlacePicker.setChecked(true);
            }
        });


        findViewById(R.id.btn_pick_place).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                sv.fullScroll(ScrollView.FOCUS_DOWN);
                cbPlacePicker.setChecked(true);
                cbGoogleMapArea.setChecked(false);


            }
        });

        findViewById(R.id.btn_check_weather_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbGoogleMapArea.isChecked()) {
                    View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.layout_confirm, null);

                    ((TextView) view.findViewById(R.id.tv)).setText("Do u want to check the weather conditions for your current location");

                    TextView textView = (TextView) view.findViewById(R.id.tvSeletedLocation);
                    textView.setVisibility(View.VISIBLE);
                    SpannableString spannablecontent = new SpannableString(nameOfCurrentLocation.toString());
                    spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, spannablecontent.length(), 0);
                    // set Text here
                    //textView.setText(nameOfCurrentLocation);
                    textView.setText(spannablecontent);
                    fireAlertDialog(view);
                }
                if (cbPlacePicker.isChecked()) {
                    View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.layout_confirm, null);
                    ((TextView) view.findViewById(R.id.tv)).setText("Do u want to check the weather conditions for the picked place");
                    TextView textView = (TextView) view.findViewById(R.id.tvSeletedLocation);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(nameOfCurrentLocation.toString());
                    fireAlertDialog(view);
                }

            }
        });

        cbPlacePicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbGoogleMapArea.setChecked(false);
            }
        });

        cbGoogleMapArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbPlacePicker.setChecked(false);

            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


    }

    private void fireAlertDialog(final View view) {

        Button ok = (Button) view.findViewById(R.id.llOptions).findViewById(R.id.btnOk);
        Button cancel = (Button) view.findViewById(R.id.llOptions).findViewById(R.id.btnCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alertDialog.dismiss();
                rlMainContaier.removeView(view);
                view.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.hide_to_bottom));
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hiding
                rlMainContaier.removeView(view);
                view.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.hide_to_bottom));

                //then show the weather info view
                refresh();


            }
        });

        rlMainContaier.addView(view);
        rlMainContaier.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));

//        builder = new AlertDialog.Builder(this);
//        builder.setView(view);
//        alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        if (!alertDialog.isShowing())
//        {
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
//            alertDialog.show();
//        }

    }

    private void refresh() {
        if (cbPlacePicker.isChecked()) {
            if (place != null) {
                float lat = (float) place.getLatLng().latitude;
                float lon = (float) place.getLatLng().longitude;
                url = url + "&lat=" + lat + "&lon=" + lon;
                fetchData(url);
            }
        }
        if (cbGoogleMapArea.isChecked()) {
            if (mLastKnownLocation != null) {
                float lat = (float) mLastKnownLocation.getLatitude();
                float lon = (float) mLastKnownLocation.getLongitude();
                url = url + "&lat=" + lat + "&lon=" + lon;
                fetchData(url);
                //Log.e("201300", url);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void fireAlert(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void showTheWeatherView() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_weather_info, null);
        TextView tvLocationName = (TextView) view.findViewById(R.id.tvLocationName);
        TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        TextView tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
        TextView tvPressure = (TextView) view.findViewById(R.id.tvPressure);
        TextView tvTemp = (TextView) view.findViewById(R.id.tvTemp);
        ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
        TextView tvUpdatedField = (TextView) view.findViewById(R.id.tvUpdatedField);
        ImageView ivRefresh = (ImageView) view.findViewById(R.id.ivRefresh);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.scale_down));
                rlMainContaier.removeView(view);
                rlMainContaier.setEnabled(true);

            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, R.anim.scale_down));
                rlMainContaier.removeView(view);
                refresh();

            }
        });

        tvLocationName.setText(nameOfCurrentLocation + ", " + country.toUpperCase(Locale.US));
        tvPressure.setText("Pressure: " + pressure + " hPa");
        tvHumidity.setText("Humidity: " + humidity + "%");
        tvStatus.setText(desc.toUpperCase(Locale.US));
        tvTemp.setText(String.format("%.2f", Double.valueOf(temp)) + " ℃");

        DateFormat df = DateFormat.getDateTimeInstance();
        String updatedOn = df.format(new Date(Long.valueOf(dt) * 1000));
        tvUpdatedField.setText("Last update: " + updatedOn);

        int icon = setWeatherIcon(Integer.valueOf(id),
                Long.valueOf(sunrise) * 1000,
                Long.valueOf(sunset) * 1000);
        ivStatus.setImageResource(icon);

        rlMainContaier.addView(view);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
        rlMainContaier.setEnabled(false);

    }

    private int setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        int icon = 0;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = R.drawable.sunny;
            } else {
                icon = R.drawable.clear_night;
            }
        } else {
            switch (id) {
                case 2:
                    icon = R.drawable.thunder;
                    break;
                case 3:
                    icon = R.drawable.heavy_rain;
                    break;
                case 7:
                    icon = R.drawable.foggy;
                    break;
                case 8:
                    icon = R.drawable.cloudy;
                    break;
                case 6:
                    icon = R.drawable.snow;
                    break;
                case 5:
                    icon = R.drawable.rainy;
                    break;
            }
        }
        return icon;
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);
                nameOfCurrentLocation = place.getName().toString();
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchData(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //get the main json obj
                            JSONObject jsonObject = new JSONObject(response.toString());
                            //getting the specific array called weather
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            //getting the first obj from the above array
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            //getting the data from the first obj of thae fetched array
                            String main = jsonObject1.getString("main");
                            desc = jsonObject1.getString("description");
                            id = jsonObject1.getString("id");
                            //getting the coord obj from the main obj
                            JSONObject jsonObject2 = jsonObject.getJSONObject("coord");
                            //getting the data from coord obj
                            String lon = jsonObject2.getString("lon");
                            String lat = jsonObject2.getString("lat");
                            String base = jsonObject.getString("base");
                            //getting the main obj for temp info
                            JSONObject jsonObject3 = jsonObject.getJSONObject("main");
                            temp = jsonObject3.getString("temp");
                            pressure = jsonObject3.getString("pressure");
                            humidity = jsonObject3.getString("humidity");
                            String temp_min = jsonObject3.getString("temp_min");
                            String temp_max = jsonObject3.getString("temp_max");
                            //getting the wind obj
                            JSONObject jsonObject4 = jsonObject.getJSONObject("wind");
                            String speed = jsonObject4.getString("speed");
                            String deg = jsonObject4.getString("deg");
                            //getting the sys object
                            JSONObject jsonObject5 = jsonObject.getJSONObject("sys");
                            country = jsonObject5.getString("country");
                            sunrise = jsonObject5.getString("sunrise");
                            sunset = jsonObject5.getString("sunset");

                            //getting the name of loc
                            nameOfCurrentLocation = jsonObject.getString("name");

                            //for updating info
                            dt = jsonObject.getString("dt");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showTheWeatherView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "on item selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        switch (item.getItemId()) {
//            case R.id.refresh:
//                break;
//            default:
//                break;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
