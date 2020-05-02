package com.mspw.staythefuckathome.main.map

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
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

    private var mMap: GoogleMap? = null
    private var mLocation: Location? = null
    private var address: String = ""
    private var currentMarker: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMapClickListener {
            mLocation?.latitude = it.latitude
            mLocation?.longitude = it.longitude
            showLocationInfo(mLocation)
        }
        getMyLocationWithPermissionCheck()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment: SupportMapFragment? = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setBtn.setOnClickListener {
            if (address.isBlank()) {
                return@setOnClickListener
            }

            val shared = SharedPreferencesUtil(this)
            val token = shared.getToken()
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
                            shared.setAddress(address)
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

        resetBtn.setOnClickListener {
            locationInformationLayout.visibility = View.GONE
            currentMarker?.remove()
        }
    }

    private fun showLocationInfo(param: Location?) {
        param?.also { location ->
            val myLocation = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(myLocation)
            markerOptions.title("My location")
            mMap?.run {
                currentMarker?.remove()
                currentMarker = addMarker(markerOptions)
                moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                animateCamera(CameraUpdateFactory.zoomTo(15f))
            }
            locationInformationLayout?.visibility = View.VISIBLE
            val list: List<Address>? = try {
                Geocoder(this).getFromLocation(
                    location.latitude,
                    location.longitude,
                    10
                )
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

            if (list.isNullOrEmpty()) {
                addressText.text = "Address search error"
                address = ""
                return@also
            }

            list.also {
                val addressLine = it[0].getAddressLine(0)
                addressText.text = addressLine
                address = "addressLine: ${location.latitude}:${location.longitude}"
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun getMyLocation() {
        val task: Task<Location> =
            LocationServices.getFusedLocationProviderClient(this).lastLocation
        task.addOnSuccessListener { location ->
            mLocation = location
            showLocationInfo(mLocation)
        }
    }

    companion object {
        private val TAG = MapActivity::class.java.simpleName
    }

}
