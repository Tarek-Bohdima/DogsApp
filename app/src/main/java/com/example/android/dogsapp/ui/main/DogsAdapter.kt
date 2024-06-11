package com.example.android.dogsapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.databinding.GridViewItemBinding

class DogsAdapter(private val listener: DogClickListener) : ListAdapter<Dog, DogsAdapter.DogsViewHolder>(
    DiffCallBack
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DogsViewHolder {
        return DogsViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DogsViewHolder, position: Int) {
        val dog = getItem(position)
        holder.bind(dog,listener)
    }

    class DogsViewHolder(private var binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dog: Dog, listener: DogClickListener) {
            binding.dogs = dog
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem == newItem
        }
    }
}

class DogClickListener(val listener: (dog: Dog) -> Unit) {
    fun onClick(dog: Dog) = listener(dog)
}