package com.mspw.staythefuckathome.main.map

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.user.UpdateAddressUser
import com.mspw.staythefuckathome.main.MainActivity
import com.mspw.staythefuckathome.main.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


@RuntimePermissions
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null;
    private var mLocation: Location? = null
    private var address: String = ""
    private var currentMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = v.map
        v.run {
            locationInformationLayout.visibility = View.GONE
            getMyLocationWithPermissionCheck()
            setBtn.setOnClickListener {
                if (address == "") return@setOnClickListener
                val token = SharedPreferencesUtil(context).getToken()
                ((activity)?.application as BaseApplication).appContainer.userRepository
                    .patchUserAddress(
                        token,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        UpdateAddressUser(address)
                    )
                    .enqueue(object : Callback<Any> {
                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            t.printStackTrace()
                            Log.e("patch error", t.message)
                        }

                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                (activity as MainActivity).toolbar.visibility = View.VISIBLE
                                (activity as MainActivity).supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment, HomeFragment()).commit()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Location setting error",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Location setting error", response.code().toString())
                            }
                        }

                    })
            }

            resetBtn.setOnClickListener {
                locationInformationLayout.visibility = View.GONE
                currentMarker?.remove()
                mMap.setOnMapClickListener {
                    mLocation?.latitude = it.latitude
                    mLocation?.longitude = it.longitude
                    showLocationInfo()
                }
            }
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocationInfo()
    }

    private fun showLocationInfo() {
        mLocation?.let {
            mMap.setOnMapClickListener {  }
            val myLocation = LatLng(it.latitude, it.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(myLocation)
            markerOptions.title("My location")
            mMap.run {
                currentMarker = addMarker(markerOptions)
                moveCamera(CameraUpdateFactory.newLatLng(myLocation))
            }
            view?.locationInformationLayout?.visibility = View.VISIBLE
            view?.run {
                val list: List<Address>? = try {
                    Geocoder(context).getFromLocation(
                        mLocation?.latitude ?: 0.0,
                        mLocation?.longitude ?: 0.0,
                        10
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                if (list != null) {
                    list.let {
                        if (list.isNotEmpty()) {
                            addressText.text = it[0].getAddressLine(0).toString()
                            address = it[0].getAddressLine(0)
                                .toString() + ":" + mLocation?.latitude + ":" + mLocation?.longitude
                        }
                    }
                } else {
                    addressText.text = "Address search error"
                }
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
        Log.e("asd", "asdasdadasdasd")
        activity?.let {
            val task: Task<Location> =
                LocationServices.getFusedLocationProviderClient(it).lastLocation
            task.addOnSuccessListener { location ->
                if (location != null) {
                    view?.searchingLocationLayout?.visibility = View.GONE
                    mLocation = location
                    mapView?.getMapAsync(this@MapFragment)

                }
            }
        }

    }
}