package com.mspw.staythefuckathome.main.map

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.user.UpdateAddressUser
import kotlinx.android.synthetic.main.activity_map.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@RuntimePermissions
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap?= null
    private var mLocation: Location? = null
    private var address: String = ""
    private var currentMarker: Marker? = null


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocationInfo()
        getMyLocationWithPermissionCheck()
        googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        val mapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        locationInformationLayout.visibility = View.GONE
        setBtn.setOnClickListener {
            if (address == "") return@setOnClickListener
            val token = SharedPreferencesUtil(this).getToken()
            (application as BaseApplication).appContainer.userRepository
                .patchUserAddress(
                    token,
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    UpdateAddressUser(address)
                )
                .enqueue(object : Callback<Any> {
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            finish()
                        } else {
                            Toast.makeText(
                                this@MapActivity,
                                "Location setting error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }

        mMap?.setOnMapClickListener {
            mLocation?.latitude = it.latitude
            mLocation?.longitude = it.longitude
            showLocationInfo()
        }

        resetBtn.setOnClickListener {
            locationInformationLayout.visibility = View.GONE
            currentMarker?.remove()
            mMap?.setOnMapClickListener {
                mLocation?.latitude = it.latitude
                mLocation?.longitude = it.longitude
                showLocationInfo()
            }
        }
    }

    private fun showLocationInfo() {
        mLocation?.let {
            mMap?.setOnMapClickListener { }

            val myLocation = LatLng(it.latitude, it.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(myLocation)
            markerOptions.title("My location")
            mMap?.run {
                currentMarker = addMarker(markerOptions)
                moveCamera(CameraUpdateFactory.newLatLng(myLocation))
            }
            locationInformationLayout?.visibility = View.VISIBLE
            val list: List<Address>? = try {
                Geocoder(this).getFromLocation(
                    mLocation?.latitude ?: 0.0,
                    mLocation?.longitude ?: 0.0,
                    10
                )
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

            if (list != null) {
                    if (list.isNotEmpty()) {
                        addressText.text = list[0].getAddressLine(0).toString()
                        address = list[0].getAddressLine(0)
                            .toString() + ":" + mLocation?.latitude + ":" + mLocation?.longitude
                    }
            } else {
                addressText.text = "Address search error"
                address = ""
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode,permissions, grantResults)
    }


    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun getMyLocation() {
        val task: Task<Location> =
            LocationServices.getFusedLocationProviderClient(this).lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                mLocation = location
                showLocationInfo()
            }
        }

    }
}
