package com.daniel.geofenceapp.adapters

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.BindingAdapter
import coil.load
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.data.GeofenceEntity
import com.daniel.geofenceapp.util.ExtensionFunctions.disable
import com.daniel.geofenceapp.util.ExtensionFunctions.enable

@BindingAdapter("loadImage")
fun ImageView.loadImage(bitmap: Bitmap){
    this.load(bitmap)
}

@BindingAdapter("parseCoordinates")
fun TextView.parseCoordinates(value: Double){
    val coordinate = String.format("%.4f", value)
    this.text = coordinate
}

@BindingAdapter("handleMotionTransition")
fun MotionLayout.handleMotionTransition(deleteImageView: ImageView){
    deleteImageView.disable()
    this.setTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(motionLayout: MotionLayout?, p1: Int, p2: Int) {}
        override fun onTransitionChange(motionLayout: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
        override fun onTransitionTrigger(motionLayout: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        override fun onTransitionCompleted(motionLayout: MotionLayout?, transition: Int) {
            if(motionLayout != null && transition == R.id.start){
                deleteImageView.disable()
            }

            else if(motionLayout != null && transition == R.id.end){
                deleteImageView.enable()
            }
        }

    })
}

@BindingAdapter("setVisibility")
fun View.setVisibility(data: List<GeofenceEntity>){
    if(data.isNullOrEmpty()){
        this.visibility = View.VISIBLE
    }else{
        this.visibility = View.INVISIBLE
    }

}