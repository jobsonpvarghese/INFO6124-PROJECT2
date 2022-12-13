package group.example.project2

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
//import com.dwijen.androidlab3.databinding.ActivityMailBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import group.example.project2.databinding.ActivityMailBinding
import java.io.IOException
import java.util.*


class MailActivity : AppCompatActivity() {

    lateinit var binding: ActivityMailBinding
    //    private  lateinit var currentLocation: Location
    private lateinit var currentLocation: Location
    private val permissionCode = 101
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_mail)



        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        binding.sendBtn.setOnClickListener {
            val email = binding.emailAddress.text.toString()

//            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

            val addresses = email.split(",".toRegex()).toTypedArray()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, addresses)

                putExtra(Intent.EXTRA_TEXT,getAddress(LatLng(currentLocation.latitude,currentLocation.longitude)))

                putExtra(Intent.EXTRA_SUBJECT,"Address")
                println(currentLocation)
            }
            startActivity(intent)
        }


    }
    private fun getCurrentLocation() {
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
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var curAddress: List<Address>? = null
        try {
            curAddress = geocoder.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)
        } catch (e1: IOException) {
            Log.e("Geocoding", "getString(R.string.problem)", e1)
        } catch (e2: IllegalArgumentException) {
            Log.e("Geocoding", "getString(R.string.invalid)"+
                    "Latitude = " + latLng!!.latitude +
                    ", Longitude = " +
                    latLng!!.longitude, e2)
        }
        // If the reverse geocode returned an address
        if (curAddress != null) {
            // Get the first address
            val address = curAddress[0]
            val addressText = String.format(
                "%s, %s, %s",
                address.getAddressLine(0), // If there's a street address, add it
                address.locality,                 // Locality is usually a city
                address.countryName)              // The country of the address
            return addressText
        }
        else
        {
            Log.e("Geocoding", "getString(R.string.noaddress)")
            return ""
        }
    }
}