package com.bdn.collinsceleb.capturefarm.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bdn.collinsceleb.capturefarm.R;
import com.bdn.collinsceleb.capturefarm.adapters.CustomInfoWindowAdapter;
import com.bdn.collinsceleb.capturefarm.adapters.PlaceAutoSuggestAdapter;
import com.bdn.collinsceleb.capturefarm.models.AddressInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener{


    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int DEFAULT_ZOOM = 5;
    private static final String KEY_CAMERA_LOCATION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private CameraPosition mCameraPosition;
    private Location mCurrentLocation;

    //widgets
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView mGps;
    private ImageView mInfo;

    //var
    private Boolean mLocationPermissionGranted = false;
    GoogleMap mMap;
    PlaceAutoSuggestAdapter placeAutoSuggestAdapter = null;
    private AddressInfo mAddress;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_LOCATION);
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        setContentView(R.layout.activity_map);


        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        autoCompleteTextView = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.place_info);
        getLocationPermission();
        updateLocationUI();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard();
                Log.d("Address : ", autoCompleteTextView.getText().toString());
                LatLng latLng = getLatLngFromAddress(autoCompleteTextView.getText().toString());

                if (latLng != null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address = getAddressFromLatLng(latLng);

                    if (address != null) {
                        try {

                            mAddress = new AddressInfo();
                            mAddress.setAddressLine(address.getAddressLine(0));

                            mAddress.setLatitude(address.getLatitude());
                            mAddress.setLongitude(address.getLongitude());

                            Log.d(TAG, "onResult: address" + mAddress.toString());

                        } catch (NullPointerException e) {
                            Log.e(TAG, "onResult: NullPointerException " + e.getMessage());
                        }

                        moveCamera(new LatLng(mAddress.getLatitude(), mAddress.getLongitude()), mAddress);
                        hideSoftKeyboard();

                    } else {
                        Log.d("Address", "Address Not Found");
                    }
                } else {
                    Log.d("Lat Lng", "Lat Lng Not Found");
                }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_LOCATION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (mCurrentLocation != null) {
                Polygon polygon = googleMap.addPolygon(new PolygonOptions().clickable(true).add(new LatLng(mCurrentLocation.getLatitude(), (mCurrentLocation.getLongitude()))));
                polygon.setTag("alpha");
                stylePolygon(polygon);
            }
            mMap.setMyLocationEnabled(true);

            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }


    }


    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);

            if (addressList != null) {
                Address singleAddress = addressList.get(0);
                LatLng latLng = new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Address getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);

            if (addresses != null) {
                Address address = addresses.get(0);
                return address;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(MapActivity.this, android.R.layout.simple_list_item_1);
        autoCompleteTextView.setAdapter(placeAutoSuggestAdapter);

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    geoLocate();
                }
                return false;
            }

        });

        mGps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked place info");

                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: "+mAddress.toString());
                        mMarker.showInfoWindow();
                    }

                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                Log.d(TAG,"LATITUDE: "+mMarker.getPosition().latitude+", LONGITUDE: "+mMarker.getPosition().longitude);
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocation");
        String searchString = autoCompleteTextView.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate, IOEXCEPTION" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            //Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    address.getAddressLine(0));
        }
    }



    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            mCurrentLocation = (Location) task.getResult();

                            if (mCurrentLocation != null)
                            moveCamera(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                                    "My Location");
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, AddressInfo addressInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) MapActivity.DEFAULT_ZOOM));
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if (addressInfo != null) {
            try {
                String snippet = "Address: " + addressInfo.getAddressLine() + "\n" +
                        "Latitude: " + addressInfo.getLatitude() + "\n" +
                        "Longitude: " + addressInfo.getLongitude();

                MarkerOptions options = new MarkerOptions()
                        .position(latLng).title(addressInfo.getAddressLine()).snippet(snippet);

                mMarker = mMap.addMarker(options);
                mMap.addMarker(options);

            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) MapActivity.DEFAULT_ZOOM));

        if (!title.equals("My Location")) {

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);


    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult : called.");
        mLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionResult: permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionResult: permission granted");
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(this, "Area type " + Objects.requireNonNull(polygon.getTag()).toString(), Toast.LENGTH_SHORT).show();
    }

    // [START maps_poly_activity_style_polygon]
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }
}