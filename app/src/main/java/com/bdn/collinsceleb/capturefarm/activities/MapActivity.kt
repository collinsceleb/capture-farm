@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.bdn.collinsceleb.capturefarm.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bdn.collinsceleb.capturefarm.R
import com.bdn.collinsceleb.capturefarm.adapters.CustomInfoWindowAdapter
import com.bdn.collinsceleb.capturefarm.adapters.PlaceAutoSuggestAdapter
import com.bdn.collinsceleb.capturefarm.models.AddressInfo
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, OnPolygonClickListener {
    private var cameraPosition: CameraPosition? = null
    private var currentLocation: Location? = null

    //widgets
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var gps: ImageView? = null
    private var info: ImageView? = null

    //var
    private var locationPermissionGranted = false
    private var map: GoogleMap? = null
    private var placeAutoSuggestAdapter: PlaceAutoSuggestAdapter? = null
    private var addressInfo: AddressInfo? = null
    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            cameraPosition =
                savedInstanceState.getParcelable(KEY_CAMERA_LOCATION)
            currentLocation =
                savedInstanceState.getParcelable(KEY_LOCATION)
        }
        setContentView(R.layout.activity_map)
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        autoCompleteTextView = findViewById(R.id.input_search)
        gps = findViewById(R.id.ic_gps)
        info = findViewById(R.id.place_info)
        locationPermission
        updateLocationUI()
        autoCompleteTextView?.onItemClickListener = OnItemClickListener { _, _, _, _ ->
            hideSoftKeyboard()
            Log.d("Address : ", autoCompleteTextView?.text.toString())
            val latLng =
                getLatLngFromAddress(autoCompleteTextView?.text.toString())
            if (latLng != null) {
                Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude)
                val address = getAddressFromLatLng(latLng)
                if (address != null) {
                    try {
                        addressInfo = AddressInfo()
                        addressInfo!!.addressLine = address.getAddressLine(0)
                        addressInfo!!.latitude = address.latitude
                        addressInfo!!.longitude = address.longitude
                        Log.d(
                            TAG,
                            "onResult: address" + addressInfo.toString()
                        )
                    } catch (e: NullPointerException) {
                        Log.e(
                            TAG,
                            "onResult: NullPointerException " + e.message
                        )
                    }
                    moveCamera(
                        LatLng(addressInfo!!.latitude, addressInfo!!.longitude),
                        addressInfo
                    )
                    hideSoftKeyboard()
                } else {
                    Log.d("Address", "Address Not Found")
                }
            } else {
                Log.d("Lat Lng", "Lat Lng Not Found")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (map != null) {
            outState.putParcelable(
                KEY_CAMERA_LOCATION,
                map!!.cameraPosition
            )
            outState.putParcelable(KEY_LOCATION, currentLocation)
            super.onSaveInstanceState(outState)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show()
        Log.d(TAG, "onMapReady: map is ready")
        map = googleMap
        if (locationPermissionGranted) {
            deviceLocation
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if (currentLocation != null) {
                val polygon = googleMap.addPolygon(
                    PolygonOptions().clickable(true).add(
                        LatLng(
                            currentLocation!!.latitude,
                            currentLocation!!.longitude
                        )
                    )
                )
                polygon.tag = "alpha"
                stylePolygon(polygon)
            }
            map!!.isMyLocationEnabled = true
            map!!.uiSettings.isMyLocationButtonEnabled = false
            init()
        }
    }

    private fun getLatLngFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(this@MapActivity)
        val addressList: List<Address>?
        return try {
            addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null) {
                val singleAddress = addressList[0]
                LatLng(singleAddress.latitude, singleAddress.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): Address? {
        val geocoder = Geocoder(this@MapActivity)
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
            addresses?.get(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun init() {
        Log.d(TAG, "init: initializing")
        placeAutoSuggestAdapter =
            PlaceAutoSuggestAdapter(this@MapActivity, android.R.layout.simple_list_item_1)
        autoCompleteTextView!!.setAdapter(placeAutoSuggestAdapter)
        autoCompleteTextView!!.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.KEYCODE_ENTER
            ) {
                geoLocate()
            }
            false
        }
        gps!!.setOnClickListener {
            Log.d(TAG, "onClick: clicked gps icon")
            deviceLocation
        }
        info!!.setOnClickListener {
            Log.d(TAG, "onClick: clicked place info")
            try {
                if (marker!!.isInfoWindowShown) {
                    marker!!.hideInfoWindow()
                } else {
                    Log.d(
                        TAG,
                        "onClick: place info: " + addressInfo.toString()
                    )
                    marker!!.showInfoWindow()
                }
            } catch (e: NullPointerException) {
                Log.e(
                    TAG,
                    "onClick: NullPointerException: " + e.message
                )
            }
        }
        map!!.setOnMapClickListener { latLng ->
            marker = map!!.addMarker(MarkerOptions().position(latLng))
            Log.d(
                TAG,
                "LATITUDE: " + (marker?.position?.latitude) + ", LONGITUDE: " + (marker?.position?.longitude)
            )
        }
        hideSoftKeyboard()
    }

    private fun geoLocate() {
        Log.d(TAG, "geoLocate: geoLocation")
        val searchString = autoCompleteTextView!!.text.toString()
        val geocoder = Geocoder(this@MapActivity)
        var list: List<Address> =
            ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: IOException) {
            Log.e(TAG, "geoLocate, IOEXCEPTION" + e.message)
        }
        if (list.isNotEmpty()) {
            val address = list[0]
            Log.d(
                TAG,
                "geoLocate: found a location: $address"
            )

            //Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show();
            moveCamera(
                LatLng(address.latitude, address.longitude),
                address.getAddressLine(0)
            )
        }
    }

    //                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
    private val deviceLocation: Unit
        get() {
            Log.d(
                TAG,
                "getDeviceLocation: getting the devices current location"
            )
            val mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
            try {
                if (locationPermissionGranted) {
                    val location: Task<Location>? =
                        mFusedLocationProviderClient.lastLocation
                    location?.addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            Log.d(
                                TAG,
                                "onComplete: found location"
                            )
                            currentLocation = task.result
                            if (currentLocation != null) moveCamera(
                                LatLng(
                                    currentLocation!!.latitude,
                                    currentLocation!!.longitude
                                ),
                                "My Location"
                            )
                        } else {
                            Log.d(
                                TAG,
                                "Current location is null. Using defaults."
                            )
                            Log.e(
                                TAG,
                                "Exception: %s",
                                task.exception
                            )
                            //                            mMap.moveCamera(CameraUpdateFactory
                            //                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            map!!.uiSettings.isMyLocationButtonEnabled = false
                        }

                    }
                }
            } catch (e: SecurityException) {
                Log.e(
                    TAG,
                    "getDeviceLocation: SecurityException " + e.message
                )
            }
        }

    private fun moveCamera(latLng: LatLng, addressInfo: AddressInfo?) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude
        )
        map!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                DEFAULT_ZOOM.toFloat()
            )
        )
        map!!.clear()
        map!!.setInfoWindowAdapter(CustomInfoWindowAdapter(this@MapActivity))
        if (addressInfo != null) {
            try {
                val snippet = """
                    Address: ${addressInfo.addressLine}
                    Latitude: ${addressInfo.latitude}
                    Longitude: ${addressInfo.longitude}
                    """.trimIndent()
                val options = MarkerOptions()
                    .position(latLng).title(addressInfo.addressLine).snippet(snippet)
                marker = map!!.addMarker(options)
                map!!.addMarker(options)
            } catch (e: NullPointerException) {
                Log.e(TAG, "moveCamera: " + e.message)
            }
        } else {
            map!!.addMarker(MarkerOptions().position(latLng))
        }
        hideSoftKeyboard()
    }

    private fun moveCamera(latLng: LatLng, title: String) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude
        )
        map!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                DEFAULT_ZOOM.toFloat()
            )
        )
        if (title != "My Location") {
            val options = MarkerOptions().position(latLng)
                .title(title)
            map!!.addMarker(options)
        }
        hideSoftKeyboard()
    }

    private fun initMap() {
        Log.d(TAG, "initMap: initializing map")
        val mapFragment =
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this@MapActivity)
    }

    private val locationPermission: Unit
        get() {
            Log.d(TAG, "getLocationPermission: getting permission")
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (ContextCompat.checkSelfPermission(
                        this.applicationContext,
                        COURSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    initMap()
                } else {
                    ActivityCompat.requestPermissions(
                        this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                ActivityCompat.requestPermissions(
                    this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult : called.")
        locationPermissionGranted = false
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionGranted = false
                        Log.d(
                            TAG,
                            "onRequestPermissionResult: permission failed"
                        )
                        return
                    }
                }
                Log.d(
                    TAG,
                    "onRequestPermissionResult: permission granted"
                )
                locationPermissionGranted = true
                initMap()
            }
        }
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
                currentLocation = null
                locationPermission
            }
        } catch (e: SecurityException) {
            Log.e(
                "Exception: %s",
                Objects.requireNonNull(e.message)
            )
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onPolygonClick(polygon: Polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        var color = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
        Toast.makeText(
            this,
            "Area type " + Objects.requireNonNull(polygon.tag)
                .toString(),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private fun stylePolygon(polygon: Polygon) {
        var type = ""
        // Get the data object stored with the polygon.
        if (polygon.tag != null) {
            type = polygon.tag.toString()
        }
        var pattern: List<PatternItem?>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    companion object {
        private const val TAG = "MapActivity"
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COURSE_LOCATION =
            Manifest.permission.ACCESS_COARSE_LOCATION
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
        private const val DEFAULT_ZOOM = 5
        private const val KEY_CAMERA_LOCATION = "camera_position"
        private const val KEY_LOCATION = "location"

        // [START maps_poly_activity_style_polygon]
        private const val COLOR_BLACK_ARGB = -0x1000000
        private const val POLYLINE_STROKE_WIDTH_PX = 12
        private const val COLOR_WHITE_ARGB = -0x1
        private const val COLOR_GREEN_ARGB = -0xc771c4
        private const val COLOR_PURPLE_ARGB = -0x7e387c
        private const val COLOR_ORANGE_ARGB = -0xa80e9
        private const val COLOR_BLUE_ARGB = -0x657db
        private const val POLYGON_STROKE_WIDTH_PX = 8
        private const val PATTERN_DASH_LENGTH_PX = 20
        private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
        private const val PATTERN_GAP_LENGTH_PX = 20
        private val DOT: PatternItem = Dot()
        private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

        // Create a stroke pattern of a gap followed by a dot.
        private val PATTERN_POLYLINE_DOTTED =
            listOf(GAP, DOT)

        // Create a stroke pattern of a gap followed by a dash.
        private val PATTERN_POLYGON_ALPHA =
            listOf(GAP, DASH)

        // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
        private val PATTERN_POLYGON_BETA =
            listOf(
                DOT,
                GAP,
                DASH,
                GAP
            )
    }
}