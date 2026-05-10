package com.example.android.dogsapp.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.databinding.FragmentFavoritesBinding
import com.example.android.dogsapp.ui.main.DogClickListener
import com.example.android.dogsapp.ui.main.DogsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = DogsAdapter(DogClickListener { dog ->
            viewModel.onDogClicked(dog)
        })
        binding.recyclerView.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { dog ->
            dog?.let {
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(it)
                )
                viewModel.onDogDetailNavigated()
            }
        }

        return binding.root
    }
}
