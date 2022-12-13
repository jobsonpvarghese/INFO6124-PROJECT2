package group.example.project2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import group.example.project2.databinding.ActivityMapsBinding
import group.example.project2.databinding.ActivityPlacesBinding
import java.io.IOException
import java.util.*

class PlacesActivity : AppCompatActivity() {
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private var mPlacesClient: PlacesClient? = null
    private val M_MAX_ENTRIES = 5
    private lateinit var mLikelyPlaceNames: Array<String>
    private lateinit var mLikelyPlaceAddresses: ArrayList<String>
    private lateinit var mLikelyPlaceLatLngs: ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val apiKey = "AIzaSyB8yofi3kZ5aVo83aWEO_b1TQVKZYJE_fc"
        Places.initialize(applicationContext, apiKey)
        mPlacesClient = Places.createClient(this)
        mLikelyPlaceNames = arrayOf<String>("","","","","")
        mLikelyPlaceAddresses = ArrayList<String>(5)
        mLikelyPlaceLatLngs = ArrayList<LatLng>(5)
            }

    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }
        val getLocation =
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                }
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
                getCurrentPlaceLikelihoods()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentPlaceLikelihoods(){
        // Use fields to define the data types to return.
        val placeFields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse: Task<FindCurrentPlaceResponse> =
            mPlacesClient!!.findCurrentPlace(request)
        placeResponse.addOnCompleteListener(this,
            OnCompleteListener<FindCurrentPlaceResponse?> { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    // Set the count, handling cases where less than 5 entries are returned.
                    val count: Int
                    if (response.placeLikelihoods.size < M_MAX_ENTRIES) {
                        count = response.placeLikelihoods.size
                    } else {
                        count = M_MAX_ENTRIES
                    }
                    println("Found a place")
                    var i = 0
                    for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                        val currPlace = placeLikelihood.place
                        mLikelyPlaceNames[i] = (currPlace.name)
                        Log.i("Places",currPlace.name)
                        mLikelyPlaceAddresses.add(currPlace.address)
                        mLikelyPlaceLatLngs.add(currPlace.latLng)
                        val currLatLng =
                            if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()
                        Log.i(
                            "Places", String.format(
                                "Place " + currPlace.name
                                        + " has likelihood: " + placeLikelihood.likelihood
                                        + " at " + currLatLng
                            )
                        )
                        i++
                        if (i > (count - 1)) {
                            break
                        }
                    }

                    // Print results to logcat
//                    println(mLikelyPlaceNames)
                } else {
                    val exception: Exception? = task.getException()
                    if (exception is ApiException) {
                        Log.e("Places", "Place not found: " + exception.statusCode)
                    }
                }
            })
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */


}