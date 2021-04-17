package com.daniel.geofenceapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.databinding.FragmentPermissionBinding
import com.daniel.geofenceapp.util.ExtensionFunctions.observeOnce
import com.daniel.geofenceapp.util.Permissions
import com.daniel.geofenceapp.viewModels.SharedViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)

        binding.continueButton.setOnClickListener {
            if(Permissions.hasLocationPermission(requireContext())){
                checkFirstLaunch()
            }
            else{
                Permissions.requestLocationPermission(this)
            }
        }

        return binding.root
    }

    private fun checkFirstLaunch() {
        sharedViewModel.readFirstLaunch.observeOnce(viewLifecycleOwner, {firstLaunch ->
            if(firstLaunch){
                findNavController().navigate(R.id.action_permissionFragment_to_add_geofence_graph)
                sharedViewModel.saveFirstLaunch(false)
            }
            else{
                findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            SettingsDialog.Builder(requireActivity()).build()
        }
        else{
            Permissions.requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>){
        Toast.makeText(requireContext(), "Permission Grandted", Toast.LENGTH_SHORT).show()
    }

}