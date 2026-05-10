package com.example.android.dogsapp.common.imaging

import android.widget.ImageView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.example.android.dogsapp.R
import javax.inject.Inject

class CoilImageLoader @Inject constructor() : ImageLoader {
    override fun load(target: ImageView, url: String?) {
        target.load(url) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.broken_image_24)
        }
    }
}
