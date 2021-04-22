package com.daniel.geofenceapp.ui.maps

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.databinding.FragmentMapsBinding
import com.daniel.geofenceapp.util.ExtensionFunctions.hide
import com.daniel.geofenceapp.util.ExtensionFunctions.show
import com.daniel.geofenceapp.util.Permissions.hasBackgroundLocationPermission
import com.daniel.geofenceapp.util.Permissions.requestBackgroundLocationPermission
import com.daniel.geofenceapp.viewModels.SharedViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener, EasyPermissions.PermissionCallbacks, GoogleMap.SnapshotReadyCallback{

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var circle: Circle
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.addGeofenceFab.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_add_geofence_graph)
        }

        binding.addGeofenceFab.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_geofencesFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
        map.isMyLocationEnabled = true
        map.setOnMapLongClickListener(this)
        map.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = false
        }
        onGeofenceReady()
        observeDatabase()


    }

    private fun zoomToSelectedLocation() {
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(sharedViewModel.geoLatLng, 10f),
                2000,
                null
        )
    }

    private fun observeDatabase(){
        sharedViewModel.readGeofences.observe(viewLifecycleOwner, { geofenceEntity ->
            map.clear()
            geofenceEntity.forEach { geofence ->
                drawCircle(LatLng(geofence.latitude, geofence.longitude), geofence.radius)
                drawMarker(LatLng(geofence.latitude, geofence.longitude), geofence.name)
            }
        })
    }

    private fun onGeofenceReady() {
        if(sharedViewModel.geoFenceReady){
            sharedViewModel.geoFenceReady = false
            sharedViewModel.geofencePrepared = true
            displayInfoMessage()
            zoomToSelectedLocation()
        }
    }

    private fun displayInfoMessage() {
        lifecycleScope.launch {
            binding.infoMessageTextView.show()
            delay(2000)
            binding.infoMessageTextView.animate().alpha(0f).duration = 800
            delay(1000)
            binding.infoMessageTextView.hide()
        }
    }

    override fun onMapLongClick(location: LatLng?) {
        if(hasBackgroundLocationPermission(requireContext())){
            if(sharedViewModel.geofencePrepared && location != null){
                stepupGeofence(location)
            }else{
                Toast.makeText(requireContext(), "You need to create a new Geofence first.", Toast.LENGTH_SHORT).show()
            }
        }else{
            requestBackgroundLocationPermission(this)
        }

    }

    private fun stepupGeofence(location: LatLng) {
        lifecycleScope.launch {
            if(sharedViewModel.checkDeviceLocationSettings(requireContext())){
                drawCircle(location, sharedViewModel.geoRadius)
                drawMarker(location, sharedViewModel.geoName)
                zoomToGeofence(circle.center, circle.radius.toFloat())

                delay(1500)
                map.snapshot(this@MapsFragment)
                delay(2000)
                sharedViewModel.addGeofenceToDatabase(location)
            }else{
                Toast.makeText(requireContext(), "Please enable location settings.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun zoomToGeofence(center: LatLng, radius: Float) {
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                        sharedViewModel.getBounds(center, radius), 10
                ), 1000, null
        )
    }

    private fun drawMarker(location: LatLng, name: String) {
         map.addMarker(
                MarkerOptions().position(location).title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

    }

    private fun drawCircle(location: LatLng, radius: Float) {
        circle = map.addCircle(
                CircleOptions().center(location).radius(radius.toDouble())
                        .strokeColor(ContextCompat.getColor(requireContext(), R.color.blue_700))
                        .fillColor(ContextCompat.getColor(requireContext(), R.color.blue_transparent))
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms[0])){
            SettingsDialog.Builder(requireActivity()).build().show()
        }else{
            requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onGeofenceReady()
        Toast.makeText(requireContext(), "Permission Granted! Long Press on the Map to add a Geofence.", Toast.LENGTH_SHORT).show()

    }

    override fun onSnapshotReady(snapshot: Bitmap?) {
        sharedViewModel.geoSnapShot = snapshot
    }


}