package com.example.android.dogsapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.android.dogsapp.R
import com.example.android.dogsapp.common.imaging.ImageLoader
import com.example.android.dogsapp.data.domain.displayBreedName
import com.example.android.dogsapp.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    @Inject lateinit var imageLoader: ImageLoader

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        val dog = viewModel.dog
        imageLoader.load(binding.dogImage, dog.imageUrl)
        binding.breedName.text = getString(R.string.breed_label, dog.displayBreedName())
        binding.favoriteButton.setOnClickListener { viewModel.toggleFavorite() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collect { renderFavorite(it) }
            }
        }

        return binding.root
    }

    private fun renderFavorite(isFavorite: Boolean) {
        val drawable = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        binding.favoriteButton.setImageResource(drawable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
