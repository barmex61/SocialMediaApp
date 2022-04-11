package com.fatih.instagramapp.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.NavOptions
import com.fatih.instagramapp.R
import com.squareup.picasso.Picasso


@BindingAdapter("android:imageUrl")
fun getImage(view:ImageView,url:String?){

    try {
        url?.let {
            Picasso.get().load(it).into(view)
        }?:Picasso.get().load(R.drawable.profile).into(view)
    }catch (e:Exception){
        println(e.message)
    }
}
fun getNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
}
