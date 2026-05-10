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
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.R
import com.example.android.dogsapp.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshDogs()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val adapter = DogsAdapter(DogClickListener { dog ->
            viewModel.onDogClicked(dog)
        })

        binding.recyclerView.adapter = adapter

        viewModel.dogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailsFragment(it))
                viewModel.onDogDetailNavigated()
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (menuItem.itemId == R.id.action_favorites) {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToFavoritesFragment()
                    )
                    true
                } else false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }
}
