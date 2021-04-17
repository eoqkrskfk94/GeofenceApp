package com.daniel.geofenceapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.util.Permissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //위치 권한 받으면 바로 지도 fragment으로 이동 하기
        if(Permissions.hasLocationPermission(this)){
            findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_permissionFragment_to_mapsFragment)
            //findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_permissionFragment_to_add_geofence_graph)

        }
    }
}