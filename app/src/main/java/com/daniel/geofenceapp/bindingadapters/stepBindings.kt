package com.daniel.geofenceapp.bindingadapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daniel.geofenceapp.util.ExtensionFunctions.hide
import com.daniel.geofenceapp.util.ExtensionFunctions.show
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("handleNetworkConnection", "handleRecyclerView", requireAll = true)
fun TextInputLayout.handleNetworkConnection(networkAvailable: Boolean, recyclerView: RecyclerView){
    if(!networkAvailable){
        this.isErrorEnabled = true
        this.error = "No Internet Connection."
        recyclerView.hide()
    }else{
        this.isErrorEnabled = false
        this.error = null
        recyclerView.show()
    }
}