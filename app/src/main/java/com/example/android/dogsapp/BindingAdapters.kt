package com.example.android.dogsapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.ui.main.DogsAdapter
import com.example.android.dogsapp.ui.main.DogsApiStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Dog>?) {
    val adapter = recyclerView.adapter as DogsAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String) {
    imgUrl.let {
        Glide.with(imgView.context)
            .load(imgUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image_24)
            )
            .into(imgView)
    }
}

@BindingAdapter("DogsApiStatus")
fun bindStatus(statusImageView: ImageView, status: DogsApiStatus?) {
    when (status) {
        DogsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        DogsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        DogsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> {}
    }
}

@BindingAdapter("breedName")
fun bindBreedName(textView: TextView, imageUrl: String?) {
    imageUrl?.let {
        // Extract the breed name from the image URL
        val breed = imageUrl.split("/")[4].replace("-", " ")
        textView.text = "Breed: $breed"
    }
}