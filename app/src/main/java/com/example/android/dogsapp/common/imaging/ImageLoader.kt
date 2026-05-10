package com.example.android.dogsapp.common.imaging

import android.widget.ImageView

interface ImageLoader {
    fun load(target: ImageView, url: String?)
}
