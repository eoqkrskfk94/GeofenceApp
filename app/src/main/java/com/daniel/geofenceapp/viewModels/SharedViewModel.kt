package com.daniel.geofenceapp.viewModels

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.daniel.geofenceapp.data.DataStoreRepository
import com.daniel.geofenceapp.data.GeofenceEntity
import com.daniel.geofenceapp.data.GeofenceRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SharedViewModel @Inject constructor(
        application: Application,
        private val dataStoreRepository: DataStoreRepository,
        private val geofenceRepository: GeofenceRepository): AndroidViewModel(application) {

    val app = application

    var geoId = 0L
    var geoName = "Default"
    var geoCountryCode = ""
    var geoLocationName = "Search City"
    var geoLatLng = LatLng(0.0, 0.0)

    var geoCitySelected = false

    var geoRadius = 500f
    var geoFenceReady = false
    var geofencePrepared = false

    //DataStore
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()

    fun saveFirstLaunch(firstLaunch: Boolean) =
            viewModelScope.launch(Dispatchers.IO) {
                dataStoreRepository.saveFirstLaunch(firstLaunch)
            }

    //Database

    val readGeofences = geofenceRepository.readGeofences.asLiveData()

    fun addGeofence(geofenceEntity: GeofenceEntity){
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.addGeofence(geofenceEntity)
        }
    }

    fun removeGeofence(geofenceEntity: GeofenceEntity){
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.removeGeofence(geofenceEntity)
        }
    }


    fun getBounds(center: LatLng, radius: Float): LatLngBounds {
        val distanceFromCenterToCorner = radius * sqrt(2.0)
        val southWestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
        val northEastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)

        return LatLngBounds(southWestCorner, northEastCorner)
    }


    fun checkDeviceLocationSettings(context: Context): Boolean {

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        }else{
            val mode : Int = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
            )

            mode != Settings.Secure.LOCATION_MODE_OFF
        }

    }



}