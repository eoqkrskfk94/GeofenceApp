package com.daniel.geofenceapp.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.viewModels.SharedViewModel
import com.google.android.material.slider.Slider

@BindingAdapter("updateSliderValueTextView", "getGeoRadius", requireAll = true)
fun Slider.updateSliderValue(textView: TextView, sharedViewModel: SharedViewModel){
    updateSliderValueTextView(sharedViewModel.geoRadius, textView)
    this.addOnChangeListener{_, value, _ ->
        sharedViewModel.geoRadius = value
        updateSliderValueTextView(sharedViewModel.geoRadius, textView)
    }
}

fun Slider.updateSliderValueTextView(geoRadius: Float, textView: TextView){
    val km = geoRadius / 1000
    if(geoRadius >= 1000f){
        textView.text = context.getString(R.string.display_kilometers, km.toString())
    }else{
        textView.text = context.getString(R.string.display_meters, geoRadius.toString())
    }
    this.value = geoRadius
}