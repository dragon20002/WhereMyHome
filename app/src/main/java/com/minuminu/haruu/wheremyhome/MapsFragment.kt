package com.minuminu.haruu.wheremyhome

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapsFragment : Fragment(), MainActivity.OnBackPressed {
    companion object {
        const val UPDATE_INTERVAL_MS = 1000L
        const val FASTEST_UPDATE_INTERVAL_MS = 500L
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        const val GPS_ENABLE_REQUEST_CODE = 1
    }
    private var needRequest = false

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var location: Location
    private val locationCallback = object:LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            locationResult?.locations?.let { locations ->
                location = locations[locations.size - 1]

                currentPosition = LatLng(location.latitude, location.longitude).also {
                    val markerTitle = getCurrentAddress(it)
                    val markerSnippet = "${it.latitude}/${it.longitude}"
                    Log.d(javaClass.name, "onLocationResult $markerSnippet")

                    // 현재 위치에 마커 생성하고 위치로 이동
                    setCurrentLocation(location, markerTitle, markerSnippet)
                }
                currentLocation = location
            }
        }
    }

    private var currentMarker: Marker? = null
    private var currentLocation: Location? = null
    private var currentPosition: LatLng? = null

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        Log.d(javaClass.name, "onMapReady")
        this.googleMap = googleMap

        // 1 기본 위치 설정
        setDefaultLocation()

        // 2 권한 확인
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

        // 3 권한 처리
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 3-1 (허용됨) 위치 업데이트 시작
            startLocationUpdates()
        } else {
            // 3-2 (거부됨) 팝업 확인 후 권한 재요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireContext()).setMessage("위치 접근 권한이 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MainActivity.PERMISSION_REQUEST_CODE
                        )
                    }
                    .show()
            }
        }

        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnMapClickListener {
            // TODO : onMapClick
            Log.d(javaClass.name, "onMapClick")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)

        LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        arguments?.getString("address")?.let {
            Log.d(javaClass.name, "maps address $it")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (checkPermission()) {
            Log.d(javaClass.name, "onStart : call fusedLocationClient.requestLocationUpdate")
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
            googleMap?.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(javaClass.name, "onStop : call stopLocationUpdates")
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE -> {
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(javaClass.name, "onActivityResult : GPS 활성화 되어있음")

                        needRequest = true
                        return
                    }
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        // Notify data changed
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "address",
            currentMarker?.title
        )
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MainActivity.PERMISSION_REQUEST_CODE && grantResults.size == MainActivity.REQUIRED_PERMISSIONS.size) {
            var checkResult = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }

            if (checkResult) {
                startLocationUpdates()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), MainActivity.REQUIRED_PERMISSIONS[0])) {
                AlertDialog.Builder(requireContext()).setMessage("위치권한 요청이 거부되었습니다.")
                    .setPositiveButton("종료") { _, _ ->
                        activity?.finish()
                    }.show()
            }
        }
    }

    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(javaClass.name, "startLocationUpdates : call showDialogForLocationServiceSetting")

            showDialogForLocationServiceStting()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(javaClass.name, "no permission")
                return
            }

            Log.d(javaClass.name, "startLocationUpdates : call fusedLocationClient.requestLocationUpdates")
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    fun getCurrentAddress(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        } catch (e: IOException) {
            // 네트워크 문제
            Log.d(javaClass.name, "Geocoder disabled")
            return "Geocoder disabled"
        } catch (e2: IllegalArgumentException) {
            Log.d(javaClass.name, "Invalid GPS location")
            return "Invalid GPS location"
        }

        addresses?.takeIf { it.isNotEmpty() }?.let {
            return it[0].getAddressLine(0).toString()
        }

        Log.d(javaClass.name, "No address")
        return "No address"
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        locationManager?.let {
            return it.isProviderEnabled(LocationManager.GPS_PROVIDER) or
                    it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        return false
    }

    fun setCurrentLocation(location: Location, markerTitle: String, markerSnippet: String) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        currentMarker?.remove()
        currentMarker = googleMap?.addMarker(MarkerOptions().apply {
            position(currentLatLng)
            title(markerTitle)
            snippet(markerSnippet)
        })

        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        googleMap?.moveCamera(cameraUpdate)
    }

    private fun setDefaultLocation() {
        currentMarker?.remove()
        currentMarker = googleMap?.addMarker(MarkerOptions().apply {
            position(DEFAULT_LOCATION)
            title("위치정보 가져올 수 없음")
            snippet("위치권한과 GPS 활성 여부 확인하세요")
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        })

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        googleMap?.moveCamera(cameraUpdate)
    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun showDialogForLocationServiceStting() {
        AlertDialog.Builder(requireContext())
            .setTitle("위치 서비스 비활성화")
            .setMessage("앱을 사용하기 위해 위치 서비스가 필요합니다.")
            .setCancelable(true)
            .setPositiveButton("설정") { _, _ ->
                val callGpsSettingIntent =
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity?.startActivityForResult(callGpsSettingIntent, GPS_ENABLE_REQUEST_CODE)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}