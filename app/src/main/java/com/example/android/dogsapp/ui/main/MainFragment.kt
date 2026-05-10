package com.example.android.dogsapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.R
import com.example.android.dogsapp.common.imaging.ImageLoader
import com.example.android.dogsapp.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject lateinit var imageLoader: ImageLoader

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshDogs()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val adapter = DogsAdapter(
            DogClickListener { dog -> viewModel.onDogClicked(dog) },
            imageLoader,
        )
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.dogs.collect { adapter.submitList(it) } }
                launch { viewModel.status.collect { renderStatus(it) } }
                launch {
                    viewModel.navigateToDetail.collect { dog ->
                        dog?.let {
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToDetailsFragment(it)
                            )
                            viewModel.onDogDetailNavigated()
                        }
                    }
                }
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                if (menuItem.itemId == R.id.action_favorites) {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToFavoritesFragment()
                    )
                    true
                } else false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun renderStatus(status: DogsApiStatus) {
        when (status) {
            DogsApiStatus.LOADING -> {
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.loading_animation)
            }
            DogsApiStatus.ERROR -> {
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.ic_connection_error)
            }
            DogsApiStatus.DONE -> {
                binding.statusImage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
