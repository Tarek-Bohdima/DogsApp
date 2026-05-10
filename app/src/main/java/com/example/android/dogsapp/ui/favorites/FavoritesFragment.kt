package com.example.android.dogsapp.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.common.imaging.ImageLoader
import com.example.android.dogsapp.databinding.FragmentFavoritesBinding
import com.example.android.dogsapp.ui.main.DogClickListener
import com.example.android.dogsapp.ui.main.DogsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    @Inject lateinit var imageLoader: ImageLoader

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        val adapter = DogsAdapter(
            DogClickListener { dog -> viewModel.onDogClicked(dog) },
            imageLoader,
        )
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.favorites.collect { adapter.submitList(it) } }
                launch {
                    viewModel.isEmpty.collect { empty ->
                        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.navigateToDetail.collect { dog ->
                        dog?.let {
                            findNavController().navigate(
                                FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(it)
                            )
                            viewModel.onDogDetailNavigated()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
