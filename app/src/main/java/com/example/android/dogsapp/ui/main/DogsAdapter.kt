package com.example.android.dogsapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.dogsapp.common.imaging.ImageLoader
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.databinding.GridViewItemBinding

class DogsAdapter(
    private val listener: DogClickListener,
    private val imageLoader: ImageLoader,
) : ListAdapter<Dog, DogsAdapter.DogsViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogsViewHolder {
        val binding = GridViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogsViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: DogsViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class DogsViewHolder(
        private val binding: GridViewItemBinding,
        private val imageLoader: ImageLoader,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dog: Dog, listener: DogClickListener) {
            imageLoader.load(binding.dogPhoto, dog.imageUrl)
            binding.dogPhoto.setOnClickListener { listener.onClick(dog) }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem == newItem
    }
}

class DogClickListener(val listener: (dog: Dog) -> Unit) {
    fun onClick(dog: Dog) = listener(dog)
}
